package com.kastack.vidyanet.routes

import com.kastack.vidyanet.services.auth.AuthController
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.authRoutes(auth: AuthController = AuthController()) {
    route("/auth") {
        auth.run {
            authRoute()
        }
    }
}
