package com.example.prmcar.data.repository

import com.example.prmcar.data.api.ApiClient
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
} 