package com.example.prmcar.data.repository

import com.example.prmcar.data.api.ApiClient
import com.example.prmcar.data.api.AuthApi
import com.example.prmcar.data.model.UserLogin
import com.example.prmcar.data.preferences.TokenManager
import kotlinx.coroutines.flow.Flow

class AuthRepository(private val tokenManager: TokenManager) {
    
    private val authApi: AuthApi = ApiClient.createAuthApi()

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val loginRequest = UserLogin(email = email, passwordHash = password)
            val response = authApi.login(loginRequest)
            
            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    // Save token and user info
                    tokenManager.saveToken(loginResponse.token)
                    tokenManager.saveUserInfo(email, "User") // Default user type
                    Result.success(loginResponse.token)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        tokenManager.clearAll()
    }

    fun isLoggedIn(): Flow<Boolean> {
        return tokenManager.isLoggedInFlow()
    }

    fun getUserEmail(): Flow<String?> {
        return tokenManager.getUserEmailFlow()
    }

    fun getUserType(): Flow<String?> {
        return tokenManager.getUserTypeFlow()
    }
} 