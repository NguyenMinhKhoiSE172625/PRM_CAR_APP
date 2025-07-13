package com.example.prmcar.data.api

import com.example.prmcar.data.model.CarRequest
import com.example.prmcar.data.model.CarResponse
import com.example.prmcar.data.model.CarsResponse
import retrofit2.Response
import retrofit2.http.*

interface CarApi {
    @GET("api/Cars")
    suspend fun getCars(
        @Query("search") search: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = null,
        @Query("minManufactureYear") minManufactureYear: Int? = null,
        @Query("maxManufactureYear") maxManufactureYear: Int? = null,
        @Query("minMileage") minMileage: Int? = null,
        @Query("maxMileage") maxMileage: Int? = null,
        @Query("color") color: String? = null,
        @Query("minAskingPrice") minAskingPrice: Double? = null,
        @Query("maxAskingPrice") maxAskingPrice: Double? = null,
        @Query("carType") carType: String? = null
    ): Response<CarsResponse>

    @GET("api/Cars/{id}")
    suspend fun getCarById(@Path("id") id: Int): Response<CarResponse>

    @POST("api/Cars")
    suspend fun createCar(@Body car: CarRequest): Response<CarResponse>

    @PUT("api/Cars/{id}")
    suspend fun updateCar(@Path("id") id: Int, @Body car: CarRequest): Response<Unit>

    @DELETE("api/Cars/{id}")
    suspend fun deleteCar(@Path("id") id: Int): Response<Unit>
} 