package com.example.prmcar.data.api

import com.example.prmcar.data.model.CarTypeRequest
import com.example.prmcar.data.model.CarTypeResponse
import com.example.prmcar.data.model.CarTypesResponse
import retrofit2.Response
import retrofit2.http.*

interface CarTypeApi {
    @GET("api/CarTypes")
    suspend fun getCarTypes(
        @Query("search") search: String? = null,
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = null
    ): Response<CarTypesResponse>

    @GET("api/CarTypes/{id}")
    suspend fun getCarTypeById(@Path("id") id: Int): Response<CarTypeResponse>

    @POST("api/CarTypes")
    suspend fun createCarType(@Body carType: CarTypeRequest): Response<CarTypeResponse>

    @PUT("api/CarTypes/{id}")
    suspend fun updateCarType(@Path("id") id: Int, @Body carType: CarTypeRequest): Response<Unit>

    @DELETE("api/CarTypes/{id}")
    suspend fun deleteCarType(@Path("id") id: Int): Response<Unit>
} 