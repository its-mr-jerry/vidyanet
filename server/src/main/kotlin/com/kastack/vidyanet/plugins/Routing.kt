package com.kastack.vidyanet.plugins


import com.kastack.vidyanet.routes.authRoutes
import com.kastack.vidyanet.routes.schoolRoutes
import com.kastack.vidyanet.routes.systemRoutes
import com.kastack.vidyanet.routes.userRoutes
import com.kastack.vidyanet.routes.roleRoutes
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respondText
import io.ktor.server.routing.IgnoreTrailingSlash
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    install(IgnoreTrailingSlash)
    routing {
        get("/") {
            call.respondText("In House erp Server is running!")
        }
        route("/api/v1") {
            authRoutes()
            authenticate("auth-jwt") {
                userRoutes()
                systemRoutes()
                schoolRoutes()
                roleRoutes()
            }
        }
    }
}
