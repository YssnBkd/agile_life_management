package com.example.agilelifemanagement.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.auth.domain.AuthRepository
import com.example.agilelifemanagement.auth.domain.model.User
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

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.login(email, password)) {
                is Result.Success -> _uiState.value = AuthUiState.Success(result.data)
                is Result.Error -> _uiState.value = AuthUiState.Error(result.message)
                is Result.Loading -> _uiState.value = AuthUiState.Loading
            }
        }
    }

    fun logout() {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.logout()) {
                is Result.Success -> _uiState.value = AuthUiState.LoggedOut
                is Result.Error -> _uiState.value = AuthUiState.Error(result.message)
                is Result.Loading -> _uiState.value = AuthUiState.Loading
            }
        }
    }

    fun checkCurrentUser() {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.getCurrentUser()) {
                is Result.Success -> _uiState.value = AuthUiState.Success(result.data)
                is Result.Error -> _uiState.value = AuthUiState.LoggedOut
                is Result.Loading -> _uiState.value = AuthUiState.Loading
            }
        }
    }
}
