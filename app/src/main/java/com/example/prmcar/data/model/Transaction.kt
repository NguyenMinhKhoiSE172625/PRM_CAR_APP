package com.example.prmcar.data.model

data class TransactionResponse(
    val transactionId: Int,
    val carId: Int,
    val buyerId: Int,
    val transactionDate: String?,
    val sellingPrice: Double,
    val transactionStatus: String?
)

data class TransactionRequest(
    val carId: Int,
    val buyerId: Int,
    val transactionDate: String?,
    val sellingPrice: Double,
    val transactionStatus: String?
)

data class TransactionsResponse(
    val pageIndex: Int?,
    val pageSize: Int?,
    val items: List<TransactionResponse>
) 