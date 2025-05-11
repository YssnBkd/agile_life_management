package com.example.agilelifemanagement.auth.domain.model

/**
 * Represents possible authentication errors.
 */
sealed class AuthError {
    object InvalidCredentials : AuthError()
    object NetworkError : AuthError()
    object UserNotFound : AuthError()
    object Unknown : AuthError()
    data class Custom(val message: String) : AuthError()
}
