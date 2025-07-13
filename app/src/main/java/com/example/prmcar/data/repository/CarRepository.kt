package com.example.prmcar.data.repository

import com.example.prmcar.data.api.ApiClient
import com.example.prmcar.data.api.CarApi
import com.example.prmcar.data.api.CarTypeApi
import com.example.prmcar.data.model.CarRequest
import com.example.prmcar.data.model.CarResponse
import com.example.prmcar.data.model.CarTypeResponse
import com.example.prmcar.data.preferences.TokenManager

class CarRepository(private val tokenManager: TokenManager) {
    
    private val carApi: CarApi = ApiClient.createCarApi(tokenManager)
    private val carTypeApi: CarTypeApi = ApiClient.createCarTypeApi(tokenManager)

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
            val response = carApi.getCars(
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
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCarById(id: Int): Result<CarResponse> {
        return try {
            val response = carApi.getCarById(id)
            
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
            val response = carApi.createCar(car)
            
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
            val response = carApi.updateCar(id, car)
            
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
            val response = carApi.deleteCar(id)
            
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
            val response = carTypeApi.getCarTypes()
            
            if (response.isSuccessful) {
                val carTypesResponse = response.body()
                Result.success(carTypesResponse?.items ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get car types: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 