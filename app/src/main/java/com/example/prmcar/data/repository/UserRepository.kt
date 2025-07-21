package com.example.prmcar.data.repository

import com.example.prmcar.data.api.ApiClient
import com.example.prmcar.data.api.UserApi
import com.example.prmcar.data.model.UserRequest
import com.example.prmcar.data.model.UserResponse
import com.example.prmcar.data.preferences.TokenManager

class UserRepository(private val tokenManager: TokenManager) {
    private val userApi: UserApi = ApiClient.createUserApi()

    private suspend fun getAuthToken(): String {
        val token = tokenManager.getToken()
        return "Bearer ${token ?: throw IllegalStateException("Auth Token not found")}" 
    }

    suspend fun getUsers(): Result<List<UserResponse>> {
        return try {
            val authToken = getAuthToken()
            val response = userApi.getUsers(authToken)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get users: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(id: Int): Result<UserResponse> {
        return try {
            val authToken = getAuthToken()
            val response = userApi.getUserById(authToken, id)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("User not found"))
            } else {
                Result.failure(Exception("Failed to get user: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUser(user: UserRequest): Result<UserResponse> {
        return try {
            val authToken = getAuthToken()
            val response = userApi.createUser(authToken, user)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Failed to create user"))
            } else {
                Result.failure(Exception("Failed to create user: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(id: Int, user: UserRequest): Result<Unit> {
        return try {
            val authToken = getAuthToken()
            val response = userApi.updateUser(authToken, id, user)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update user: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(id: Int): Result<Unit> {
        return try {
            val authToken = getAuthToken()
            val response = userApi.deleteUser(authToken, id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete user: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 