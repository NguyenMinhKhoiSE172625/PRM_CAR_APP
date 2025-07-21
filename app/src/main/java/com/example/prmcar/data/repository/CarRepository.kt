package com.example.prmcar.data.repository

import android.util.Log
import com.example.prmcar.data.api.ApiClient
import com.example.prmcar.data.api.CarApi
import com.example.prmcar.data.api.CarTypeApi
import com.example.prmcar.data.model.CarRequest
import com.example.prmcar.data.model.CarResponse
import com.example.prmcar.data.model.CarTypeResponse
import com.example.prmcar.data.preferences.TokenManager
import kotlinx.coroutines.CancellationException

class CarRepository(private val tokenManager: TokenManager) {
    
    private val carApi: CarApi = ApiClient.createCarApi()
    private val carTypeApi: CarTypeApi = ApiClient.createCarTypeApi()

    private suspend fun getAuthToken(): String {
        val token = tokenManager.getToken()
        return "Bearer ${token ?: throw IllegalStateException("Auth Token not found")}"
    }

    suspend fun getCars(
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
        carType: String? = null
    ): Result<List<CarResponse>> {
        return try {
            val authToken = getAuthToken()
            val response = carApi.getCars(
                token = authToken,
                search, sortBy, page, pageSize,
                minManufactureYear, maxManufactureYear,
                minMileage, maxMileage, color,
                minAskingPrice, maxAskingPrice, carType
            )
            
            if (response.isSuccessful) {
                val carsResponse = response.body()
                Result.success(carsResponse?.items ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get cars: ${response.message()}"))
            }
        } catch (e: CancellationException) {
            Log.e("REPO_ERROR", "Coroutine was cancelled in getCars", e)
            throw e
        } catch (e: Exception) {
            Log.e("REPO_ERROR", "Error getting cars", e)
            Result.failure(e)
        }
    }

    suspend fun getCarById(id: Int): Result<CarResponse> {
        return try {
            val authToken = getAuthToken()
            val response = carApi.getCarById(token = authToken, id = id)
            
            if (response.isSuccessful) {
                val car = response.body()
                if (car != null) {
                    Result.success(car)
                } else {
                    Result.failure(Exception("Car not found"))
                }
            } else {
                Result.failure(Exception("Failed to get car: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCar(car: CarRequest): Result<CarResponse> {
        return try {
            val authToken = getAuthToken()
            val response = carApi.createCar(token = authToken, car = car)
            
            if (response.isSuccessful) {
                val createdCar = response.body()
                if (createdCar != null) {
                    Result.success(createdCar)
                } else {
                    Result.failure(Exception("Failed to create car"))
                }
            } else {
                Result.failure(Exception("Failed to create car: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCar(id: Int, car: CarRequest): Result<Unit> {
        return try {
            val authToken = getAuthToken()
            val response = carApi.updateCar(token = authToken, id = id, car = car)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update car: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCar(id: Int): Result<Unit> {
        return try {
            val authToken = getAuthToken()
            val response = carApi.deleteCar(token = authToken, id = id)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete car: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCarTypes(): Result<List<CarTypeResponse>> {
        return try {
            val authToken = getAuthToken()
            val response = carTypeApi.getCarTypes(token = authToken)
            
            if (response.isSuccessful) {
                val carTypesResponse = response.body()
                Result.success(carTypesResponse?.items ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get car types: ${response.message()}"))
            }
        } catch (e: CancellationException) {
            Log.e("REPO_ERROR", "Coroutine was cancelled in getCarTypes", e)
            throw e
        } catch (e: Exception) {
            Log.e("REPO_ERROR", "Error getting car types", e)
            Result.failure(e)
        }
    }
} 