package com.example.agilelifemanagement.auth.domain.usecase

import com.example.agilelifemanagement.auth.domain.AuthRepository
import com.example.agilelifemanagement.auth.domain.model.User
import com.example.agilelifemanagement.domain.model.Result
import javax.inject.Inject

/**
 * Use case for retrieving current user information.
 * Follows Clean Architecture principles by encapsulating the user retrieval business logic.
 */
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<User> {
        return authRepository.getCurrentUser()
    }
}
