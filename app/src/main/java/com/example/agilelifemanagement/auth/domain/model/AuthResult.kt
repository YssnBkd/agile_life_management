package com.example.agilelifemanagement.auth.domain.model

/**
 * Represents the result of an authentication operation.
 * Success contains a User, Failure contains an AuthError.
 */
sealed class AuthResult {
    data class Success(val user: User): AuthResult()
    data class Failure(val error: AuthError): AuthResult()
}
