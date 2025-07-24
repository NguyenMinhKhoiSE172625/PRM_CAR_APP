package com.example.prmcar.data.api

import com.example.prmcar.data.model.TransactionRequest
import com.example.prmcar.data.model.TransactionResponse
import com.example.prmcar.data.model.TransactionsResponse
import retrofit2.Response
import retrofit2.http.*

interface TransactionApi {
    @GET("api/Transactions")
    suspend fun getTransactions(@Header("Authorization") token: String): Response<TransactionsResponse>

    @GET("api/Transactions/{id}")
    suspend fun getTransactionById(@Header("Authorization") token: String, @Path("id") id: Int): Response<TransactionResponse>

    @GET("api/Transactions/GetByUserID")
    suspend fun getTransactionsByUserId(@Header("Authorization") token: String, @Query("id") userId: Int): Response<TransactionsResponse>

    @POST("api/Transactions")
    suspend fun createTransaction(@Header("Authorization") token: String, @Body transaction: TransactionRequest): Response<TransactionResponse>

    @PUT("api/Transactions/{id}")
    suspend fun updateTransaction(@Header("Authorization") token: String, @Path("id") id: Int, @Body transaction: TransactionRequest): Response<Unit>

    @DELETE("api/Transactions/{id}")
    suspend fun deleteTransaction(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    @POST("api/Transactions/purchase")
    suspend fun purchaseCar(@Header("Authorization") token: String, @Body request: PurchaseCarRequest): Response<TransactionResponse>
}

data class PurchaseCarRequest(
    val carId: Int,
    val buyerId: Int,
    val price: Double
)