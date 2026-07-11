package com.kastack.vidyanet.routes

import com.kastack.vidyanet.services.school.SchoolController
import com.kastack.vidyanet.services.schoolSettings.SchoolSettingsController
import io.ktor.server.routing.*

fun Route.schoolRoutes(
    schoolController: SchoolController = SchoolController(),
) {
    schoolController.run {
        schoolRoutes()
    }

}
