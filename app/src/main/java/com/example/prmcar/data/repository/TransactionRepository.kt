package com.example.prmcar.data.repository

import com.example.prmcar.data.api.ApiClient
import com.example.prmcar.data.api.PurchaseCarRequest
import com.example.prmcar.data.api.TransactionApi
import com.example.prmcar.data.model.TransactionRequest
import com.example.prmcar.data.model.TransactionResponse
import com.example.prmcar.data.model.TransactionsResponse
import com.example.prmcar.data.preferences.TokenManager

class TransactionRepository(private val tokenManager: TokenManager) {
    private val transactionApi: TransactionApi = ApiClient.createTransactionApi()

    private suspend fun getAuthToken(): String {
        val token = tokenManager.getToken()
        return "Bearer ${token ?: throw IllegalStateException("Auth Token not found")}" 
    }

    suspend fun getTransactions(): Result<List<TransactionResponse>> {
        return try {
            val authToken = getAuthToken()
            val response = transactionApi.getTransactions(authToken)
            if (response.isSuccessful) {
                Result.success(response.body()?.items ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get transactions: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTransactionById(id: Int): Result<TransactionResponse> {
        return try {
            val authToken = getAuthToken()
            val response = transactionApi.getTransactionById(authToken, id)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Transaction not found"))
            } else {
                Result.failure(Exception("Failed to get transaction: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTransactionsByUserId(userId: Int): Result<List<TransactionResponse>> {
        return try {
            val authToken = getAuthToken()
            val response = transactionApi.getTransactionsByUserId(authToken, userId)
            if (response.isSuccessful) {
                Result.success(response.body()?.items ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get user transactions: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTransaction(transaction: TransactionRequest): Result<TransactionResponse> {
        return try {
            val authToken = getAuthToken()
            val response = transactionApi.createTransaction(authToken, transaction)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Failed to create transaction"))
            } else {
                Result.failure(Exception("Failed to create transaction: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTransaction(id: Int, transaction: TransactionRequest): Result<Unit> {
        return try {
            val authToken = getAuthToken()
            val response = transactionApi.updateTransaction(authToken, id, transaction)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update transaction: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(id: Int): Result<Unit> {
        return try {
            val authToken = getAuthToken()
            val response = transactionApi.deleteTransaction(authToken, id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete transaction: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun purchaseCar(carId: Int, buyerId: Int, price: Double): Result<TransactionResponse> {
        return try {
            val authToken = getAuthToken()
            println("DEBUG: Starting purchase - carId=$carId, buyerId=$buyerId, price=$price")

            // Dùng create transaction endpoint (đã test thành công trong Swagger)
            println("DEBUG: Using create transaction endpoint...")
            // Tạo current date với format đúng
            val currentDateTime = java.time.LocalDateTime.now()
            val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val formattedDate = currentDateTime.format(formatter)

            val transactionRequest = TransactionRequest(
                carId = carId,
                buyerId = buyerId,
                transactionDate = formattedDate,
                sellingPrice = price,
                transactionStatus = "Completed"
            )
            val response = transactionApi.createTransaction(authToken, transactionRequest)
            println("DEBUG: Create transaction response code: ${response.code()}")
            if (response.isSuccessful) {
                println("DEBUG: Create transaction successful!")
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Transaction failed"))
            } else {
                println("DEBUG: Create transaction failed: ${response.message()}")
                Result.failure(Exception("Failed to create transaction: ${response.message()}"))
            }
        } catch (e: Exception) {
            println("DEBUG: Exception in purchaseCar: ${e.message}")
            Result.failure(e)
        }
    }
}