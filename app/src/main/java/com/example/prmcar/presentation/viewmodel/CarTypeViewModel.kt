package com.example.prmcar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prmcar.data.model.CarTypeRequest
import com.example.prmcar.data.model.CarTypeResponse
import com.example.prmcar.data.repository.CarTypeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI State cho CarType
data class CarTypeUiState(
    val isLoading: Boolean = false,
    val carTypes: List<CarTypeResponse> = emptyList(),
    val selectedCarType: CarTypeResponse? = null,
    val errorMessage: String? = null,
    val isActionSuccess: Boolean = false
)

class CarTypeViewModel(private val carTypeRepository: CarTypeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CarTypeUiState())
    val uiState: StateFlow<CarTypeUiState> = _uiState.asStateFlow()

    fun loadCarTypes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isActionSuccess = false)
            carTypeRepository.getCarTypes()
                .onSuccess { carTypes ->
                    _uiState.value = _uiState.value.copy(isLoading = false, carTypes = carTypes, errorMessage = null)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                }
        }
    }

    fun getCarTypeById(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, selectedCarType = null)
            carTypeRepository.getCarTypeById(id)
                .onSuccess { carType ->
                    _uiState.value = _uiState.value.copy(isLoading = false, selectedCarType = carType)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                }
        }
    }

    fun createCarType(carType: CarTypeRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isActionSuccess = false)
            carTypeRepository.createCarType(carType)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isActionSuccess = true)
                    loadCarTypes()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                }
        }
    }

    fun updateCarType(id: Int, carType: CarTypeRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isActionSuccess = false)
            carTypeRepository.updateCarType(id, carType)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isActionSuccess = true)
                    loadCarTypes()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                }
        }
    }

    fun deleteCarType(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isActionSuccess = false)
            carTypeRepository.deleteCarType(id)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isActionSuccess = true)
                    loadCarTypes()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                }
        }
    }

    fun clearSelectedCarType() {
        _uiState.value = _uiState.value.copy(selectedCarType = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearActionSuccess() {
        _uiState.value = _uiState.value.copy(isActionSuccess = false)
    }
} 