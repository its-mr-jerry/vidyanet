package com.kastack.vidyanet.routes

import com.kastack.vidyanet.services.role.RoleController
import io.ktor.server.routing.*

fun Route.roleRoutes(roleController: RoleController = RoleController()) {
    roleController.run {
        roleRoutes()
    }
}
