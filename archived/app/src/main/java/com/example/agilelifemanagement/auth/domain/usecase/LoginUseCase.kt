package com.example.agilelifemanagement.auth.domain.usecase

import com.example.agilelifemanagement.auth.domain.AuthRepository
import com.example.agilelifemanagement.auth.domain.model.User
import com.example.agilelifemanagement.domain.model.Result
import javax.inject.Inject

/**
 * Use case for user login.
 * Follows Clean Architecture principles by encapsulating the login business logic.
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.login(email, password)
    }
}
