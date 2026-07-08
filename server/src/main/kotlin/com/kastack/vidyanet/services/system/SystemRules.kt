package com.kastack.vidyanet.services.system

import com.kastack.vidyanet.models.user.UserType
import com.kastack.vidyanet.plugins.authorize
import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put

abstract class SystemRules {
    abstract suspend fun getSystemConfig(call: ApplicationCall)
    abstract suspend fun updateSystemConfig(call: ApplicationCall)

    fun Route.systemRoute() {
        get("/config") {
            getSystemConfig(call)
        }
        
        authorize(UserType.PLATFORM_OWNER) {
            put("/config") {
                updateSystemConfig(call)
            }
        }
    }
}
