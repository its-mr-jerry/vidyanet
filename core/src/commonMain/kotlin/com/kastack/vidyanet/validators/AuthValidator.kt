package com.kastack.vidyanet.validators

import com.kastack.vidyanet.models.auth.LoginRequest
import com.kastack.vidyanet.models.auth.VerifyOtpRequest

object AuthValidator {

    fun validateSendOtp(request: LoginRequest) {
        validatePhone(request.phone)
    }

    fun validateVerifyOtp(request: VerifyOtpRequest) {
        validatePhone(request.phone)
        validateOtp(request.otp)
    }

    fun validatePhone(phone: String) {
        if (phone.isBlank()) {
            throw ValidationException("Phone number is required")
        }
        if (!phone.all { it.isDigit() } || phone.length < 10) {
            throw ValidationException("Invalid phone number format. Provide at least 10 digits.")
        }
    }

    fun validateOtp(otp: String) {
        if (otp.isBlank()) {
            throw ValidationException("OTP is required")
        }
        if (otp.length != 6 || !otp.all { it.isDigit() }) {
            throw ValidationException("OTP must be exactly 6 digits")
        }
    }
}
