package com.example.agilelifemanagement.domain.model

/**
 * A generic class that holds a value or an error.
 * @param <T> The type of the value.
 */
sealed class Result<out T> {
    /**
     * Represents successful operation with data.
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Represents failed operation with error message and optional cause.
     */
    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : Result<Nothing>()

    companion object {
        /**
         * Create a successful result.
         */
        fun <T> success(data: T): Result<T> = Success(data)

        /**
         * Create an error result.
         */
        fun error(message: String, cause: Throwable? = null): Result<Nothing> = 
            Error(message, cause)
    }

    /**
     * Returns true if this is a Success.
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns true if this is an Error.
     */
    val isError: Boolean get() = this is Error

    /**
     * Gets the value or null if it's an error.
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }

    /**
     * Execute the given function if this is a Success.
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Execute the given function if this is an Error.
     */
    inline fun onError(action: (String, Throwable?) -> Unit): Result<T> {
        if (this is Error) action(message, cause)
        return this
    }

    /**
     * Transform success data with the given function.
     */
    inline fun <R> map(transform: (T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(message, cause)
        }
    }

    /**
     * Execute code depending on whether this is a Success or Error.
     */
    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (String, Throwable?) -> R
    ): R {
        return when (this) {
            is Success -> onSuccess(data)
            is Error -> onError(message, cause)
        }
    }
}
