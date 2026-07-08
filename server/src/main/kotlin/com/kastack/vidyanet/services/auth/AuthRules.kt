package com.kastack.vidyanet.services.auth

import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

abstract class AuthRules {
    abstract suspend fun sendOtp(call: ApplicationCall)
    abstract suspend fun verifyOtp(call: ApplicationCall)


    fun Route.authRoute() {
        post("/send-otp") {
            sendOtp(call)
        }
        post("/verify-otp") {
            verifyOtp(call)
        }
    }


}
