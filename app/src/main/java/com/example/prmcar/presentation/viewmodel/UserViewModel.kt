package com.example.prmcar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prmcar.data.model.UserRequest
import com.example.prmcar.data.model.UserResponse
import com.example.prmcar.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI State cho User
data class UserUiState(
    val isLoading: Boolean = false,
    val users: List<UserResponse> = emptyList(),
    val selectedUser: UserResponse? = null,
    val errorMessage: String? = null,
    val isActionSuccess: Boolean = false
)

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isActionSuccess = false)
            userRepository.getUsers()
                .onSuccess { users ->
                    _uiState.value = _uiState.value.copy(isLoading = false, users = users, errorMessage = null)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                }
        }
    }

    fun getUserById(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, selectedUser = null)
            userRepository.getUserById(id)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(isLoading = false, selectedUser = user)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                }
        }
    }

    fun createUser(user: UserRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isActionSuccess = false)
            userRepository.createUser(user)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isActionSuccess = true)
                    loadUsers()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                }
        }
    }

    fun updateUser(id: Int, user: UserRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isActionSuccess = false)
            userRepository.updateUser(id, user)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isActionSuccess = true)
                    loadUsers()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isActionSuccess = false)
            userRepository.deleteUser(id)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isActionSuccess = true)
                    loadUsers()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                }
        }
    }

    fun clearSelectedUser() {
        _uiState.value = _uiState.value.copy(selectedUser = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearActionSuccess() {
        _uiState.value = _uiState.value.copy(isActionSuccess = false)
    }
} 