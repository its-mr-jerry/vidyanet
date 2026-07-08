package com.kastack.vidyanet.data.repositories

import com.kastack.vidyanet.models.auth.LoginRequest
import com.kastack.vidyanet.models.auth.LoginResponse
import com.kastack.vidyanet.models.auth.VerifyOtpRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

interface AuthRepository {
    suspend fun sendOtp(request: LoginRequest): Result<Unit>
    suspend fun verifyOtp(request: VerifyOtpRequest): Result<LoginResponse>
}

class AuthRepositoryImpl(private val client: HttpClient) : AuthRepository {
    override suspend fun sendOtp(request: LoginRequest): Result<Unit> = try {
        val response = client.post("auth/send-otp") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status == HttpStatusCode.OK) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to send OTP: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun verifyOtp(request: VerifyOtpRequest): Result<LoginResponse> = try {
        val response = client.post("auth/verify-otp") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status == HttpStatusCode.OK) {
            Result.success(response.body())
        } else {
            Result.failure(Exception("Invalid OTP: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
