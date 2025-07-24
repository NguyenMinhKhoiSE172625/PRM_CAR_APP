package com.example.prmcar.data.repository

import com.example.prmcar.data.api.ApiClient
import com.example.prmcar.data.api.AuthApi
import com.example.prmcar.data.model.UserLogin
import com.example.prmcar.data.preferences.TokenManager
import kotlinx.coroutines.flow.Flow
import com.auth0.android.jwt.JWT

class AuthRepository(private val tokenManager: TokenManager) {
    
    private val authApi: AuthApi = ApiClient.createAuthApi()

    private fun decodeToken(token: String): Pair<String?, Int?> {
        val jwt = JWT(token)

        // Parse role từ Microsoft claims
        val role = jwt.getClaim("role").asString()
            ?: jwt.getClaim("http://schemas.microsoft.com/ws/2008/06/identity/claims/role").asString()

        // Parse userId từ Microsoft nameidentifier claim
        val userIdString = jwt.getClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier").asString()
        val userId = userIdString?.toIntOrNull()

        println("DEBUG: JWT decode - role=$role, userIdString=$userIdString, userId=$userId")
        return Pair(role, userId)
    }

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val loginRequest = UserLogin(email = email, passwordHash = password)
            val response = authApi.login(loginRequest)
            
            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    tokenManager.saveToken(loginResponse.token)
                    val (role, userId) = decodeToken(loginResponse.token)
                    println("DEBUG: Decoded token - role=$role, userId=$userId")
                    if (role != null) tokenManager.saveUserInfo(email, role)
                    if (userId != null) {
                        tokenManager.saveUserId(userId)
                        println("DEBUG: Saved userId=$userId")
                    } else {
                        println("DEBUG: userId is null!")
                    }
                    Result.success(loginResponse.token)
                } else {
                    Result.failure(Exception("Empty response body or login failed"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Login failed: ${response.message()} - $errorMsg"))
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

    fun getUserId(): Flow<Int?> {
        return tokenManager.getUserIdFlow()
    }
} 