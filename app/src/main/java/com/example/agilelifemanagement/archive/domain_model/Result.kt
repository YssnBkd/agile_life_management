package com.example.agilelifemanagement.domain.model

/**
 * A generic class that holds a value with its loading status.
 * @param <T> Type of the data being loaded.
 *
 * This implements the comprehensive error handling strategy defined in the app's
 * global rules, using Kotlin's Result class pattern for handling network issues,
 * database errors, and validation failures.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
    
    /**
     * Returns true if this result is a success.
     */
    val isSuccess: Boolean get() = this is Success
    
    /**
     * Returns true if this result is an error.
     */
    val isError: Boolean get() = this is Error
    
    /**
     * Returns true if this result is loading.
     */
    val isLoading: Boolean get() = this is Loading
    
    /**
     * Returns the data if this is a success, or null otherwise.
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * Maps the success value using the given transform function.
     */
    fun <R> map(transform: (T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> this
            is Loading -> Loading
        }
    }
    
    /**
     * Executes the given block if this is a success.
     */
    inline fun onSuccess(block: (T) -> Unit): Result<T> {
        if (this is Success) {
            block(data)
        }
        return this
    }
    
    /**
     * Executes the given block if this is an error.
     */
    inline fun onError(block: (String, Exception?) -> Unit): Result<T> {
        if (this is Error) {
            block(message, exception)
        }
        return this
    }
    
    /**
     * Executes the given block if this is loading.
     */
    inline fun onLoading(block: () -> Unit): Result<T> {
        if (this is Loading) {
            block()
        }
        return this
    }
    
    companion object {
        /**
         * Wraps a suspending operation in a Result, catching any exceptions.
         */
        suspend fun <T> of(block: suspend () -> T): Result<T> {
            return try {
                Success(block())
            } catch (e: Exception) {
                Error(e.message ?: "Unknown error occurred", e)
            }
        }
    }
}
