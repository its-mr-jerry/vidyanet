package com.kastack.vidyanet.routes

import com.kastack.vidyanet.services.notificationSettings.NotificationSettingsController
import io.ktor.server.routing.Route

fun Route.NotificationSettingsRoute(controller: NotificationSettingsController = NotificationSettingsController()) {
    controller.run {
        notificationSettingsRoutes()
    }
}
