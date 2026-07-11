package com.kastack.vidyanet.routes

import com.kastack.vidyanet.services.academicSettings.AcademicSettingsController
import io.ktor.server.routing.Route

fun Route.AcademicSettingsRoute(academicSettingsController: AcademicSettingsController = AcademicSettingsController()) {
    academicSettingsController.run {
        academicSettingsRoutes()
    }
}
