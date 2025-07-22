package com.example.prmcar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prmcar.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userEmail: String? = null,
    val userType: String? = null,
    val userId: Int? = null,
    val errorMessage: String? = null
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            authRepository.isLoggedIn().collect { isLoggedIn ->
                _uiState.value = _uiState.value.copy(isLoggedIn = isLoggedIn)
            }
        }
        
        viewModelScope.launch {
            authRepository.getUserEmail().collect { email ->
                _uiState.value = _uiState.value.copy(userEmail = email)
            }
        }
        
        viewModelScope.launch {
            authRepository.getUserType().collect { userType ->
                _uiState.value = _uiState.value.copy(userType = userType)
            }
        }
        
        viewModelScope.launch {
            authRepository.getUserId().collect { userId ->
                _uiState.value = _uiState.value.copy(userId = userId)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            authRepository.login(email, password)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        errorMessage = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Login failed"
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = _uiState.value.copy(
                isLoggedIn = false,
                userEmail = null,
                userType = null
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
} 