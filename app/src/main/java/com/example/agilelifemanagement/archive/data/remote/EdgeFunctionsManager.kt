package com.example.agilelifemanagement.data.remote

import android.util.Log
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.util.NetworkMonitor
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.functions.functions
import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.http.ContentType
import kotlinx.serialization.encodeToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for invoking Supabase Edge Functions for server-side validation and complex operations.
 */
@Singleton
class EdgeFunctionsManager @Inject constructor(
    private val supabaseManager: SupabaseManager,
    private val networkMonitor: NetworkMonitor,
    private val json: Json
) {
    /**
     * Invoke a Supabase Edge Function.
     * 
     * @param functionName Name of the Edge Function to invoke
     * @param requestBody Request body to send (will be serialized to JSON)
     * @param T Type of expected response
     * @return Result containing the response or error
     */
    suspend fun <T> invokeFunction(
        functionName: String,
        requestBody: Any,
        deserializer: kotlinx.serialization.DeserializationStrategy<T>
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            // Check network connectivity
            if (!networkMonitor.isOnline()) {
                return@withContext Result.Error("No network connection")
            }
            
            // Check authentication
            val authState = supabaseManager.authState.value
            if (authState !is AuthState.Authenticated) {
                return@withContext Result.Error("User not authenticated")
            }
            
            val client = supabaseManager.getClient()
            
            // Serialize request body to JSON string
            val requestBodyJson = json.encodeToString(requestBody)
            
            // Invoke the function
            Log.d(TAG, "Invoking Edge Function: $functionName")
            val response = client.functions.invoke(functionName) {
                headers {
                    append(io.ktor.http.HttpHeaders.ContentType, io.ktor.http.ContentType.Application.Json.toString())
                }
                setBody(requestBodyJson)
            }
            
            Log.d(TAG, "Edge Function response: $response")
            
            // Parse the response
            val responseData = json.decodeFromString(deserializer, response.body<String>())
            return@withContext Result.Success(responseData)
        } catch (e: Exception) {
            Log.e(TAG, "Error invoking Edge Function $functionName: ${e.message}", e)
            Result.Error("Error invoking Edge Function: ${e.message}")
        }
    }
    
    /**
     * Validate a new goal for conflicts before saving.
     * 
     * @param goalId Goal ID to validate
     * @param title Goal title
     * @param deadline Goal deadline timestamp
     * @param userId User ID
     * @return Result containing validation result
     */
    suspend fun validateGoal(
        goalId: String,
        title: String,
        deadline: Long?,
        userId: String
    ): Result<GoalValidationResponse> {
        val request = GoalValidationRequest(
            goalId = goalId,
            title = title,
            deadline = deadline,
            userId = userId
        )
        
        return invokeFunction(
            "validate-goal",
            request,
            GoalValidationResponse.serializer()
        )
    }
    
    /**
     * Validate a task for conflicts before saving.
     * 
     * @param taskId Task ID to validate
     * @param title Task title
     * @param dueDate Task due date timestamp
     * @param priority Task priority
     * @param userId User ID
     * @return Result containing validation result
     */
    suspend fun validateTask(
        taskId: String,
        title: String,
        dueDate: Long?,
        priority: String,
        userId: String
    ): Result<TaskValidationResponse> {
        val request = TaskValidationRequest(
            taskId = taskId,
            title = title,
            dueDate = dueDate,
            priority = priority,
            userId = userId
        )
        
        return invokeFunction(
            "validate-task",
            request,
            TaskValidationResponse.serializer()
        )
    }
    
    /**
     * Request to validate a goal.
     */
    @Serializable
    data class GoalValidationRequest(
        @SerialName("goal_id") val goalId: String,
        val title: String,
        val deadline: Long?,
        @SerialName("user_id") val userId: String
    )
    
    /**
     * Response from goal validation.
     */
    @Serializable
    data class GoalValidationResponse(
        val valid: Boolean,
        val conflicts: List<Conflict> = emptyList(),
        val message: String? = null
    )
    
    /**
     * Request to validate a task.
     */
    @Serializable
    data class TaskValidationRequest(
        @SerialName("task_id") val taskId: String,
        val title: String,
        @SerialName("due_date") val dueDate: Long?,
        val priority: String,
        @SerialName("user_id") val userId: String
    )
    
    /**
     * Response from task validation.
     */
    @Serializable
    data class TaskValidationResponse(
        val valid: Boolean,
        val conflicts: List<Conflict> = emptyList(),
        val message: String? = null
    )
    
    /**
     * Represents a conflict found during validation.
     */
    @Serializable
    data class Conflict(
        val entity: String,
        @SerialName("entity_id") val entityId: String,
        val reason: String,
        val details: String? = null
    )
    
    companion object {
        private const val TAG = "EdgeFunctionsManager"
    }
}
