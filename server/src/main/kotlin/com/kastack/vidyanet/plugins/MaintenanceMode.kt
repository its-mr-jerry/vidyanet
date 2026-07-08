package com.kastack.vidyanet.plugins

import com.kastack.vidyanet.services.system.SystemService
import com.kastack.vidyanet.models.user.UserType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.path
import io.ktor.server.response.*

val MaintenanceModePlugin = createApplicationPlugin(name = "MaintenanceModePlugin") {
    val systemService = SystemService()
    var lastCheck = 0L
    var isMaintenanceCached = false
    val cacheDuration = 5000L

    onCall { call ->
        val path = call.request.path()

        if (path.contains("/system/config") || path.contains("/auth/") || path.contains("/users/me")) return@onCall

        val now = System.currentTimeMillis()
        if (now - lastCheck > cacheDuration) {
            isMaintenanceCached = try {
                systemService.getSystemConfig().isMaintenanceMode
            } catch (e: Exception) {
                false 
            }
            lastCheck = now
        }
        if (isMaintenanceCached) {
            val principal = call.principal<JWTPrincipal>()
            val userRole = principal?.payload?.getClaim("role")?.asString()
            if (userRole != UserType.PLATFORM_OWNER.name) {
                call.respond(
                    HttpStatusCode.ServiceUnavailable,
                    mapOf("message" to "System is under maintenance. Please try again later.")
                )
            }
        }
    }
}
