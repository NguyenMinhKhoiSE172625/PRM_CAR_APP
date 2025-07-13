package com.example.prmcar.data.model

data class UserResponse(
    val userId: Int,
    val username: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String?,
    val address: String?,
    val userType: String,
    val registrationDate: String?
)

data class UserRequest(
    val username: String,
    val passwordHash: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String?,
    val address: String?,
    val registrationDate: String?
)

data class UserLogin(
    val email: String,
    val passwordHash: String
)

data class LoginResponse(
    val token: String
) 