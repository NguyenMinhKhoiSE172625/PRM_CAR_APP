package com.example.prmcar.data.repository

import com.example.prmcar.data.api.ApiClient
import com.example.prmcar.data.api.CarTypeApi
import com.example.prmcar.data.model.CarTypeRequest
import com.example.prmcar.data.model.CarTypeResponse
import com.example.prmcar.data.model.CarTypesResponse
import com.example.prmcar.data.preferences.TokenManager

class CarTypeRepository(private val tokenManager: TokenManager) {
    private val carTypeApi: CarTypeApi = ApiClient.createCarTypeApi()

    private suspend fun getAuthToken(): String {
        val token = tokenManager.getToken()
        return "Bearer ${token ?: throw IllegalStateException("Auth Token not found")}" 
    }

    suspend fun getCarTypes(): Result<List<CarTypeResponse>> {
        return try {
            val authToken = getAuthToken()
            val response = carTypeApi.getCarTypes(authToken)
            if (response.isSuccessful) {
                Result.success(response.body()?.items ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get car types: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCarTypeById(id: Int): Result<CarTypeResponse> {
        return try {
            val authToken = getAuthToken()
            val response = carTypeApi.getCarTypeById(authToken, id)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("CarType not found"))
            } else {
                Result.failure(Exception("Failed to get car type: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCarType(carType: CarTypeRequest): Result<CarTypeResponse> {
        return try {
            val authToken = getAuthToken()
            val response = carTypeApi.createCarType(authToken, carType)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Failed to create car type"))
            } else {
                Result.failure(Exception("Failed to create car type: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCarType(id: Int, carType: CarTypeRequest): Result<Unit> {
        return try {
            val authToken = getAuthToken()
            val response = carTypeApi.updateCarType(authToken, id, carType)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update car type: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCarType(id: Int): Result<Unit> {
        return try {
            val authToken = getAuthToken()
            val response = carTypeApi.deleteCarType(authToken, id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete car type: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 