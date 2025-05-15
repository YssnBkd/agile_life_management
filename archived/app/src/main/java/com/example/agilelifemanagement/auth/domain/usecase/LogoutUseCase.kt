package com.example.agilelifemanagement.auth.domain.usecase

import com.example.agilelifemanagement.auth.domain.AuthRepository
import com.example.agilelifemanagement.domain.model.Result
import javax.inject.Inject

/**
 * Use case for user logout.
 * Follows Clean Architecture principles by encapsulating the logout business logic.
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}
