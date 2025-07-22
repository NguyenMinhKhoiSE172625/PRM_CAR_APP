package com.example.prmcar.data.api

import com.example.prmcar.data.model.UserRequest
import com.example.prmcar.data.model.UserResponse
import com.example.prmcar.data.model.UserListResponse
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @GET("api/Users")
    suspend fun getUsers(
        @Header("Authorization") token: String,
        @Query("userType") userType: String? = null
    ): Response<UserListResponse>

    @GET("api/Users/{id}")
    suspend fun getUserById(@Header("Authorization") token: String, @Path("id") id: Int): Response<UserResponse>

    @POST("api/Users")
    suspend fun createUser(@Header("Authorization") token: String, @Body user: UserRequest): Response<UserResponse>

    @PUT("api/Users/{id}")
    suspend fun updateUser(@Header("Authorization") token: String, @Path("id") id: Int, @Body user: UserRequest): Response<Unit>

    @DELETE("api/Users/{id}")
    suspend fun deleteUser(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>
} 