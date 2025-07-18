package com.example.prmcar.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prmcar.data.model.CarRequest
import com.example.prmcar.data.model.CarResponse
import com.example.prmcar.data.model.CarTypeResponse
import com.example.prmcar.data.repository.CarRepository
import com.example.prmcar.data.utils.ImageUploadManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CarUiState(
    val isLoading: Boolean = false,
    val cars: List<CarResponse> = emptyList(),
    val carTypes: List<CarTypeResponse> = emptyList(),
    val selectedCar: CarResponse? = null,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val isUploadingImage: Boolean = false
)

class CarViewModel(
    private val carRepository: CarRepository,
    private val imageUploadManager: ImageUploadManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CarUiState())
    val uiState: StateFlow<CarUiState> = _uiState.asStateFlow()

    init {
        loadCars()
        loadCarTypes()
    }

    fun loadCars(
        search: String? = null,
        sortBy: String? = null,
        page: Int? = null,
        pageSize: Int? = null,
        minManufactureYear: Int? = null,
        maxManufactureYear: Int? = null,
        minMileage: Int? = null,
        maxMileage: Int? = null,
        color: String? = null,
        minAskingPrice: Double? = null,
        maxAskingPrice: Double? = null,
        carType: String? = null,
        isRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.value = _uiState.value.copy(isRefreshing = true, errorMessage = null)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            }
            
            carRepository.getCars(
                search, sortBy, page, pageSize,
                minManufactureYear, maxManufactureYear,
                minMileage, maxMileage, color,
                minAskingPrice, maxAskingPrice, carType
            )
                .onSuccess { cars ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        cars = cars,
                        errorMessage = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = exception.message ?: "Failed to load cars"
                    )
                }
        }
    }

    fun loadCarTypes() {
        viewModelScope.launch {
            carRepository.getCarTypes()
                .onSuccess { carTypes ->
                    _uiState.value = _uiState.value.copy(carTypes = carTypes)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = exception.message ?: "Failed to load car types"
                    )
                }
        }
    }

    fun getCarById(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            carRepository.getCarById(id)
                .onSuccess { car ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        selectedCar = car,
                        errorMessage = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to load car details"
                    )
                }
        }
    }

    fun createCar(car: CarRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            carRepository.createCar(car)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    // Refresh the cars list
                    loadCars()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to create car"
                    )
                }
        }
    }

    fun updateCar(id: Int, car: CarRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            carRepository.updateCar(id, car)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    // Refresh the cars list
                    loadCars()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to update car"
                    )
                }
        }
    }

    fun deleteCar(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            carRepository.deleteCar(id)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    // Refresh the cars list
                    loadCars()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to delete car"
                    )
                }
        }
    }

    fun uploadImage(context: Context, imageUri: Uri, carId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingImage = true, errorMessage = null)
            
            imageUploadManager.uploadImage(context, imageUri, carId)
                .onSuccess { imageUrl ->
                    _uiState.value = _uiState.value.copy(isUploadingImage = false)
                    // Refresh car details to show new image
                    getCarById(carId)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isUploadingImage = false,
                        errorMessage = exception.message ?: "Failed to upload image"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSelectedCar() {
        _uiState.value = _uiState.value.copy(selectedCar = null)
    }

    fun refresh() {
        loadCars(isRefresh = true)
    }
} 