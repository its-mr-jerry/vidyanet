package com.kastack.vidyanet.routes

import com.kastack.vidyanet.services.user.UserController
import io.ktor.server.routing.*


fun Route.userRoutes(userController: UserController = UserController()) {
    route("/users") {
        userController.run {
            UserRoutes()
        }
    }
}
