package com.kastack.vidyanet.services.auth

import com.kastack.vidyanet.models.auth.LoginRequest
import com.kastack.vidyanet.models.auth.VerifyOtpRequest
import com.kastack.vidyanet.validators.AuthValidator
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

class AuthController(private val authServices: AuthServices = AuthServices()) : AuthRules() {
    override suspend fun sendOtp(call: ApplicationCall) {
        val request = call.receive<LoginRequest>()
        AuthValidator.validateSendOtp(request)
        authServices.sendOtp(request)
        call.respond(HttpStatusCode.OK, mapOf("message" to "OTP sent successfully"))
    }

    override suspend fun verifyOtp(call: ApplicationCall) {
        val request = call.receive<VerifyOtpRequest>()
        AuthValidator.validateVerifyOtp(request)
        val response = authServices.verifyOtp(request)
        call.respond(response)
    }


}
