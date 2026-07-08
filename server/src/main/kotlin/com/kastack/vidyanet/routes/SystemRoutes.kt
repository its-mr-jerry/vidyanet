package com.kastack.vidyanet.routes


import com.kastack.vidyanet.services.system.SystemController
import io.ktor.server.routing.*

fun Route.systemRoutes(systemController: SystemController = SystemController()) {
    route("/system") {
        systemController.run {
            systemRoute()
        }
    }
}
