package com.kastack.vidyanet.routes

import com.kastack.vidyanet.services.schoolSettings.SchoolSettingsController
import io.ktor.server.routing.Route

fun Route.SchoolSettingsRoute(schoolSettingsController: SchoolSettingsController = SchoolSettingsController()) {
    schoolSettingsController.run {
        schoolSettingsRoutes()
    }
}