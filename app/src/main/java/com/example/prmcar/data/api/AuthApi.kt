package com.example.prmcar.data.api

import com.example.prmcar.data.model.LoginResponse
import com.example.prmcar.data.model.UserLogin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/Authentication/Login")
    suspend fun login(@Body loginRequest: UserLogin): Response<LoginResponse>
} 