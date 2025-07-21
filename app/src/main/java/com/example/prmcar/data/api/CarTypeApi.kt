package com.example.prmcar.data.api

import com.example.prmcar.data.model.CarTypeRequest
import com.example.prmcar.data.model.CarTypeResponse
import com.example.prmcar.data.model.CarTypesResponse
import retrofit2.Response
import retrofit2.http.*

interface CarTypeApi {
    @GET("api/CarTypes")
    suspend fun getCarTypes(@Header("Authorization") token: String): Response<CarTypesResponse>

    @GET("api/CarTypes/{id}")
    suspend fun getCarTypeById(@Header("Authorization") token: String, @Path("id") id: Int): Response<CarTypeResponse>

    @POST("api/CarTypes")
    suspend fun createCarType(@Header("Authorization") token: String, @Body carType: CarTypeRequest): Response<CarTypeResponse>

    @PUT("api/CarTypes/{id}")
    suspend fun updateCarType(@Header("Authorization") token: String, @Path("id") id: Int, @Body carType: CarTypeRequest): Response<Unit>

    @DELETE("api/CarTypes/{id}")
    suspend fun deleteCarType(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>
} 