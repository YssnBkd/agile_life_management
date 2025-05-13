package com.example.agilelifemanagement.util

import android.util.Log
import com.example.agilelifemanagement.domain.model.Result
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.exceptions.HttpRequestException
import java.net.UnknownHostException
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized error handling for the app.
 * Provides common error handling logic, error categorization, and error state management.
 */
@Singleton
class ErrorHandler @Inject constructor() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // Global error state flow for UI components to observe
    private val _errorState = MutableStateFlow<ErrorState>(ErrorState.None)
    val errorState: StateFlow<ErrorState> = _errorState.asStateFlow()
    
    /**
     * Handle and categorize an exception, returning an appropriate Result.Error.
     */
    fun <T> handleException(e: Exception, operation: String): Result<T> {
        Log.e(TAG, "Error in $operation: ${e.message}", e)
        
        return when (e) {
            is RestException -> Result.Error(
                "Server error: ${parseRestError(e)}",
                Exception(e.message)
            )
            is BadRequestRestException -> Result.Error(
                "Bad request: ${e.message ?: "Unknown request error"}",
                Exception(e.message)
            )
            is HttpRequestException -> Result.Error(
                "HTTP request error: ${e.message ?: "Unknown HTTP error"}",
                Exception(e.message)
            )
            is UnknownHostException,
            is ConnectException,
            is SocketTimeoutException -> Result.Error(
                "Network error: Please check your internet connection",
                e
            )
            is kotlinx.coroutines.CancellationException -> {
                // This is an expected exception when a coroutine is cancelled
                // Don't log or report this
                Result.Error("Operation cancelled", e)
            }
            else -> Result.Error(
                "Unexpected error: ${e.message ?: "Unknown error"}",
                e
            )
        }
    }
    
    /**
     * Parse error details from a Supabase REST exception.
     */
    private fun parseRestError(e: RestException): String {
        return try {
            e.error ?: "Unknown REST error"
        } catch (ex: Exception) {
            "Error parsing REST error: ${ex.message}"
        }
    }
    
    /**
     * Parse error details from a Postgrest exception.
     */
    private fun parsePostgrestError(e: Exception): String {
        return try {
            e.message ?: "Unknown database error"
        } catch (ex: Exception) {
            "Error parsing Postgrest error: ${ex.message}"
        }
    }
    
    /**
     * Update the global error state. Can be used to show snackbars, error dialogs, etc.
     */
    fun setError(error: ErrorState) {
        scope.launch {
            _errorState.value = error
        }
    }
    
    /**
     * Clear the current error state.
     */
    fun clearError() {
        scope.launch {
            _errorState.value = ErrorState.None
        }
    }
    
    companion object {
        private const val TAG = "ErrorHandler"
    }
    
    /**
     * Represents the error state for the app.
     */
    sealed class ErrorState {
        object None : ErrorState()
        
        data class Message(val message: String) : ErrorState()
        
        data class NetworkError(
            val message: String = "Network connection unavailable",
            val retryable: Boolean = true
        ) : ErrorState()
        
        data class AuthError(
            val message: String = "Authentication required",
            val requiresLogin: Boolean = true
        ) : ErrorState()
        
        data class SyncError(
            val message: String,
            val entityId: String? = null,
            val entityType: String? = null
        ) : ErrorState()
        
        data class ValidationError(
            val message: String,
            val field: String? = null
        ) : ErrorState()
        
        data class StorageError(
            val message: String,
            val operation: String? = null,
            val filePath: String? = null
        ) : ErrorState()
        
        data class GeneralError(
            val message: String,
            val exception: Throwable? = null
        ) : ErrorState()
    }
}
