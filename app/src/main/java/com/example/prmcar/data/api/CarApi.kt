package com.example.prmcar.data.api

import com.example.prmcar.data.model.CarRequest
import com.example.prmcar.data.model.CarResponse
import com.example.prmcar.data.model.CarsResponse
import retrofit2.Response
import retrofit2.http.*

interface CarApi {
    @GET("api/Cars")
    suspend fun getCars(
        @Header("Authorization") token: String,
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
    suspend fun getCarById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<CarResponse>

    @POST("api/Cars")
    suspend fun createCar(
        @Header("Authorization") token: String,
        @Body car: CarRequest
    ): Response<CarResponse>

    @PUT("api/Cars/{id}")
    suspend fun updateCar(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body car: CarRequest
    ): Response<Unit>

    @DELETE("api/Cars/{id}")
    suspend fun deleteCar(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
} 