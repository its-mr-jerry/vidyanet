package com.kastack.vidyanet.models.auth

import com.kastack.vidyanet.models.user.UserDto
import com.kastack.vidyanet.models.user.UserStatus
import com.kastack.vidyanet.models.user.UserType
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val phone: String,
    val userType: UserType
)



@Serializable
data class VerifyOtpRequest(
    val phone: String,
    val otp: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: UserDto
)

