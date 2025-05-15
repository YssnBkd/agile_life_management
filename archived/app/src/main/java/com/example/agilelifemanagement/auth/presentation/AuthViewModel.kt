package com.example.agilelifemanagement.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.auth.domain.model.User
import com.example.agilelifemanagement.auth.domain.usecase.GetCurrentUserUseCase
import com.example.agilelifemanagement.auth.domain.usecase.LoginUseCase
import com.example.agilelifemanagement.auth.domain.usecase.LogoutUseCase
import com.example.agilelifemanagement.domain.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    object LoggedOut : AuthUiState()
}

/**
 * ViewModel for authentication screens.
 * 
 * Following Clean Architecture principles, this ViewModel interacts with the domain layer
 * through use cases rather than directly with repositories.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = loginUseCase(email, password)) {
                is Result.Success -> _uiState.value = AuthUiState.Success(result.data)
                is Result.Error -> _uiState.value = AuthUiState.Error(result.message)
                is Result.Loading -> _uiState.value = AuthUiState.Loading
            }
        }
    }

    fun logout() {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = logoutUseCase()) {
                is Result.Success -> _uiState.value = AuthUiState.LoggedOut
                is Result.Error -> _uiState.value = AuthUiState.Error(result.message)
                is Result.Loading -> _uiState.value = AuthUiState.Loading
            }
        }
    }

    fun checkCurrentUser() {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> _uiState.value = AuthUiState.Success(result.data)
                is Result.Error -> _uiState.value = AuthUiState.LoggedOut
                is Result.Loading -> _uiState.value = AuthUiState.Loading
            }
        }
    }
}
