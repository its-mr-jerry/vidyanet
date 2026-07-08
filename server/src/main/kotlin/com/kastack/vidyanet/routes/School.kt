package com.kastack.vidyanet.routes

import com.kastack.vidyanet.services.school.SchoolController
import io.ktor.server.routing.*

fun Route.schoolRoutes(schoolController: SchoolController = SchoolController()) {
    schoolController.run {
        schoolRoutes()
    }
}
