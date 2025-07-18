package com.example.prmcar.di

import android.content.Context
import com.example.prmcar.data.api.ApiClient
import com.example.prmcar.data.api.AuthApi
import com.example.prmcar.data.api.CarApi
import com.example.prmcar.data.api.CarTypeApi
import com.example.prmcar.data.api.ImageUploadService
import com.example.prmcar.data.preferences.TokenManager
import com.example.prmcar.data.repository.AuthRepository
import com.example.prmcar.data.repository.CarRepository
import com.example.prmcar.data.utils.ImageUploadManager
import com.example.prmcar.presentation.viewmodel.AuthViewModel
import com.example.prmcar.presentation.viewmodel.CarViewModel

object AppModule {
    // Hàm khởi tạo AuthViewModel
    fun createAuthViewModel(context: Context): AuthViewModel {
        val tokenManager = TokenManager(context)
        val authApi = ApiClient.createAuthApi()
        val authRepository = AuthRepository(tokenManager)
        return AuthViewModel(authRepository)
    }

    // Hàm khởi tạo CarViewModel
    fun createCarViewModel(context: Context): CarViewModel {
        val tokenManager = TokenManager(context)
        val carRepository = CarRepository(tokenManager)
        // ImageUploadService không cần token
        val retrofit = ApiClient.createRetrofit(tokenManager)
        val imageUploadService = retrofit.create(ImageUploadService::class.java)
        val imageUploadManager = ImageUploadManager(imageUploadService)
        return CarViewModel(carRepository, imageUploadManager)
    }
} 