package com.example.agilelifemanagement.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.example.agilelifemanagement.data.local.entity.PendingOperation
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.util.NetworkMonitor
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for synchronizing files between local storage and Supabase Storage.
 * Implements upload, download, caching, and automatic retry mechanisms.
 */
@Singleton
class StorageSyncManager @Inject constructor(
    private val context: Context,
    private val supabaseManager: SupabaseManager,
    private val networkMonitor: NetworkMonitor,
    private val syncManager: SyncManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Track ongoing uploads and downloads
    private val ongoingOperations = ConcurrentHashMap<String, OperationStatus>()
    
    // Track file upload/download progress
    private val _transferProgress = MutableStateFlow<Map<String, Int>>(emptyMap())
    val transferProgress = _transferProgress.asStateFlow()
    
    // Default bucket name
    private val defaultBucket = "agile-life-files"
    
    // Maximum retry attempts
    private val maxRetryCount = 3
    
    // Cache directory
    private val cacheDir by lazy {
        File(context.cacheDir, "supabase_files").apply {
            if (!exists()) mkdirs()
        }
    }
    
    /**
     * Upload a file to Supabase Storage.
     * 
     * @param uri Local URI of the file to upload
     * @param bucketName Storage bucket name (optional, defaults to "agile-life-files")
     * @param path Path within the bucket where the file should be stored
     * @param contentType MIME type of the file
     * @param overwriteExisting Whether to overwrite existing file with the same path
     * @return Result containing the full path to the uploaded file
     */
    suspend fun uploadFile(
        uri: Uri,
        bucketName: String = defaultBucket,
        path: String,
        contentType: String,
        overwriteExisting: Boolean = false
    ): Result<String> = withContext(Dispatchers.IO) {
        val operationId = UUID.randomUUID().toString()
        ongoingOperations[operationId] = OperationStatus.IN_PROGRESS
        
        try {
            // Check authentication
            val authState = supabaseManager.authState.value
            if (authState !is AuthState.Authenticated) {
                ongoingOperations[operationId] = OperationStatus.FAILED
                return@withContext Result.Error("User not authenticated")
            }
            
            // Check network
            if (!networkMonitor.isOnline()) {
                // Save for later upload
                queueFileForUpload(uri, bucketName, path, contentType, overwriteExisting)
                ongoingOperations[operationId] = OperationStatus.QUEUED
                return@withContext Result.Error("Network unavailable, file queued for upload")
            }
            
            val file = when (uri.scheme) {
                "file" -> uri.toFile()
                "content" -> {
                    val inputStream = context.contentResolver.openInputStream(uri)
                        ?: return@withContext Result.Error("Could not open input stream for uri: $uri")

                    val tempFile = createTempFile(inputStream, getFileNameFromUri(uri) ?: "temp_${System.currentTimeMillis()}")
                    inputStream.close()
                    tempFile
                }
                else -> return@withContext Result.Error("Unsupported URI scheme: ${uri.scheme}")
            }
            
            try {
                val client = supabaseManager.getClient()
                
                // Update progress
                updateProgress(operationId, 0)
                
                // Upload file with progress tracking
                val uploadResult = client.storage.from(bucketName).upload(
                    path = path,
                    data = file.readBytes()
                ) {
                    this.contentType = ContentType.parse(contentType)
                    this.upsert = overwriteExisting
                }
                
                // Clean up temporary file if created
                if (uri.scheme == "content" && file.exists()) {
                    file.delete()
                }
                
                // Complete the operation
                updateProgress(operationId, 100)
                ongoingOperations[operationId] = OperationStatus.COMPLETED
                
                val fullPath = "$bucketName/$path"
                Log.d(TAG, "File uploaded successfully: $fullPath")
                Result.Success(fullPath)
            } catch (e: CancellationException) {
                Log.e(TAG, "Upload cancelled: ${e.message}", e)
                ongoingOperations[operationId] = OperationStatus.FAILED
                
                // Queue for retry if not caused by cancellation
                queueFileForUpload(uri, bucketName, path, contentType, overwriteExisting)
                
                Result.Error("Upload cancelled")
            } catch (e: Throwable) {
                Log.e(TAG, "Storage error uploading file: ${e.message}", e)
                ongoingOperations[operationId] = OperationStatus.FAILED
                
                // Queue for retry if not caused by cancellation
                if (e !is CancellationException) {
                    queueFileForUpload(uri, bucketName, path, contentType, overwriteExisting)
                }
                
                Result.Error("Storage error: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in uploadFile: ${e.message}", e)
            ongoingOperations[operationId] = OperationStatus.FAILED
            Result.Error("Unexpected error: ${e.message}")
        }
    }
    
    /**
     * Download a file from Supabase Storage.
     * 
     * @param bucketName Storage bucket name
     * @param path Path within the bucket to the file
     * @param destinationFile Local file to save the download to (optional)
     * @return Result containing the local Uri of the downloaded file
     */
    suspend fun downloadFile(
        bucketName: String = defaultBucket,
        path: String,
        destinationFile: File? = null
    ): Result<Uri> = withContext(Dispatchers.IO) {
        val operationId = UUID.randomUUID().toString()
        ongoingOperations[operationId] = OperationStatus.IN_PROGRESS
        
        try {
            // Check authentication
            val authState = supabaseManager.authState.value
            if (authState !is AuthState.Authenticated) {
                ongoingOperations[operationId] = OperationStatus.FAILED
                return@withContext Result.Error("User not authenticated")
            }
            
            // Check network
            if (!networkMonitor.isOnline()) {
                ongoingOperations[operationId] = OperationStatus.FAILED
                return@withContext Result.Error("Network unavailable")
            }
            
            // Check if file is already cached
            val fileName = path.substringAfterLast('/')
            val cachedFile = File(cacheDir, "${bucketName}_$path".replace('/', '_'))
            
            if (cachedFile.exists()) {
                // File is cached, return it
                ongoingOperations[operationId] = OperationStatus.COMPLETED
                return@withContext Result.Success(cachedFile.toUri())
            }
            
            // Destination file
            val targetFile = destinationFile ?: cachedFile
            
            // Ensure parent directories exist
            targetFile.parentFile?.mkdirs()
            
            try {
                val client = supabaseManager.getClient()
                
                // Update progress
                updateProgress(operationId, 0)
                
                // Download file
                val downloadedBytes = client.storage.from(bucketName).downloadPublic(path)
                
                // Write the downloaded bytes to the target file
                targetFile.writeBytes(downloadedBytes)
                
                // Complete the operation
                updateProgress(operationId, 100)
                ongoingOperations[operationId] = OperationStatus.COMPLETED
                
                Log.d(TAG, "File downloaded successfully: $path to ${targetFile.absolutePath}")
                Result.Success(targetFile.toUri())
            } catch (e: CancellationException) {
                Log.e(TAG, "Download cancelled: ${e.message}", e)
                ongoingOperations[operationId] = OperationStatus.FAILED
                Result.Error("Download cancelled")
            } catch (e: Throwable) {
                Log.e(TAG, "Storage error downloading file: ${e.message}", e)
                ongoingOperations[operationId] = OperationStatus.FAILED
                Result.Error("Storage error: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in downloadFile: ${e.message}", e)
            ongoingOperations[operationId] = OperationStatus.FAILED
            Result.Error("Unexpected error: ${e.message}")
        }
    }
    
    /**
     * Get a public URL for a file in Supabase Storage.
     * 
     * @param bucketName Storage bucket name
     * @param path Path within the bucket to the file
     * @param expiresIn Expiration time in seconds (default: 3600 = 1 hour)
     * @return Result containing the public URL
     */
    suspend fun getPublicUrl(
        bucketName: String = defaultBucket,
        path: String,
        expiresIn: Int = 3600
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Check authentication
            val authState = supabaseManager.authState.value
            if (authState !is AuthState.Authenticated) {
                return@withContext Result.Error("User not authenticated")
            }
            
            // Check network
            if (!networkMonitor.isOnline()) {
                return@withContext Result.Error("Network unavailable")
            }
            
            val client = supabaseManager.getClient()
            val url = client.storage.from(bucketName).publicUrl(path)
            Result.Success(url)
        } catch (e: CancellationException) {
            Log.e(TAG, "Get public URL cancelled: ${e.message}", e)
            Result.Error("Get public URL cancelled")
        } catch (e: Throwable) {
            Log.e(TAG, "Error getting public URL: ${e.message}", e)
            Result.Error("Error getting URL: ${e.message}")
        }
    }
    
    /**
     * Delete a file from Supabase Storage.
     * 
     * @param bucketName Storage bucket name
     * @param path Path within the bucket to the file
     * @return Result indicating success or failure
     */
    suspend fun deleteFile(
        bucketName: String = defaultBucket,
        path: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Check authentication
            val authState = supabaseManager.authState.value
            if (authState !is AuthState.Authenticated) {
                return@withContext Result.Error("User not authenticated")
            }
            
            // Handle offline case
            if (!networkMonitor.isOnline()) {
                queueFileForDeletion(bucketName, path)
                return@withContext Result.Error("Network unavailable, deletion queued")
            }
            
            val client = supabaseManager.getClient()
            client.storage.from(bucketName).delete(path)
            
            // Delete from cache if exists
            val cachedFile = File(cacheDir, "${bucketName}_$path".replace('/', '_'))
            if (cachedFile.exists()) {
                cachedFile.delete()
            }
            
            Log.d(TAG, "File deleted successfully: $bucketName/$path")
            Result.Success(Unit)
        } catch (e: CancellationException) {
            Log.e(TAG, "Delete cancelled: ${e.message}", e)
            Result.Error("Delete cancelled")
        } catch (e: Throwable) {
            Log.e(TAG, "Error deleting file: ${e.message}", e)
            
            // Queue for retry if network error
            if (!networkMonitor.isOnline()) {
                queueFileForDeletion(bucketName, path)
            }
            
            Result.Error("Error deleting file: ${e.message}")
        }
    }
    
    /**
     * List files in a bucket or folder.
     * 
     * @param bucketName Storage bucket name
     * @param path Path within the bucket (optional)
     * @return Result containing list of file paths
     */
    suspend fun listFiles(
        bucketName: String = defaultBucket,
        path: String? = null
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            // Check authentication
            val authState = supabaseManager.authState.value
            if (authState !is AuthState.Authenticated) {
                return@withContext Result.Error("User not authenticated")
            }
            
            // Check network
            if (!networkMonitor.isOnline()) {
                return@withContext Result.Error("Network unavailable")
            }
            
            val client = supabaseManager.getClient()
            val files = client.storage.from(bucketName).list(path ?: "")
            Result.Success(files.map { it.name })
        } catch (e: Exception) {
            Log.e(TAG, "Error listing files: ${e.message}", e)
            Result.Error("Error listing files: ${e.message}")
        }
    }
    
    /**
     * Process any queued file operations that were postponed due to network issues.
     */
    suspend fun processQueuedOperations() {
        // Implementation would process pending operations stored in a local database
        // This would be called when network becomes available
        Log.d(TAG, "Processing queued storage operations")
        
        // Actual implementation would retrieve queued operations from a database
        // and execute them one by one
    }
    
    /**
     * Queue a file upload for later when network is available.
     */
    private fun queueFileForUpload(
        uri: Uri,
        bucketName: String,
        path: String,
        contentType: String,
        overwriteExisting: Boolean
    ) {
        // In a full implementation, this would store the operation in a local database
        // For this example, we'll just log it
        Log.d(TAG, "Queued file upload: $uri to $bucketName/$path")
        
        // Listen for network connectivity and retry
        scope.launch {
            syncManager.scheduleSyncOperation(
                entityId = path,
                entityType = "storage_upload",
                operation = PendingOperation.CREATE
            )
        }
    }
    
    /**
     * Queue a file deletion for later when network is available.
     */
    private fun queueFileForDeletion(bucketName: String, path: String) {
        // In a full implementation, this would store the operation in a local database
        // For this example, we'll just log it
        Log.d(TAG, "Queued file deletion: $bucketName/$path")
        
        // Listen for network connectivity and retry
        scope.launch {
            syncManager.scheduleSyncOperation(
                entityId = path,
                entityType = "storage_delete",
                operation = PendingOperation.DELETE
            )
        }
    }
    
    /**
     * Update progress for a file transfer operation.
     */
    private fun updateProgress(operationId: String, progress: Int) {
        val currentMap = _transferProgress.value.toMutableMap()
        currentMap[operationId] = progress
        _transferProgress.value = currentMap
        
        if (progress >= 100) {
            // Remove completed operations after a short delay
            scope.launch {
                kotlinx.coroutines.delay(2000) // 2 seconds to show completed status
                val updatedMap = _transferProgress.value.toMutableMap()
                updatedMap.remove(operationId)
                _transferProgress.value = updatedMap
            }
        }
    }
    
    /**
     * Create a temporary file from an input stream.
     */
    private fun createTempFile(inputStream: InputStream, fileName: String): File {
        val tempFile = File(cacheDir, "upload_$fileName")
        
        FileOutputStream(tempFile).use { output ->
            val buffer = ByteArray(4 * 1024) // 4KB buffer
            var read: Int
            
            while (inputStream.read(buffer).also { read = it } != -1) {
                output.write(buffer, 0, read)
            }
            
            output.flush()
        }
        
        return tempFile
    }
    
    /**
     * Get a file name from a content URI.
     */
    private fun getFileNameFromUri(uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex("_display_name")
            if (nameIndex >= 0 && it.moveToFirst()) {
                it.getString(nameIndex)
            } else null
        }
    }
    
    /**
     * Clear the cache of downloaded files.
     */
    fun clearCache() {
        cacheDir.listFiles()?.forEach { it.delete() }
        Log.d(TAG, "File cache cleared")
    }
    
    companion object {
        private const val TAG = "StorageSyncManager"
        
        /**
         * Status of a file operation.
         */
        enum class OperationStatus {
            QUEUED,
            IN_PROGRESS,
            COMPLETED,
            FAILED
        }
    }
}
