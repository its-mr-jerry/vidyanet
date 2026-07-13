package com.kastack.vidyanet

import com.kastack.vidyanet.plugins.MaintenanceModePlugin
import com.kastack.vidyanet.plugins.configureHTTP
import com.kastack.vidyanet.plugins.configureRouting
import com.kastack.vidyanet.plugins.configureSecurity
import com.kastack.vidyanet.plugins.configureSerialization
import com.kastack.vidyanet.plugins.configureStatusPages
import com.kastack.vidyanet.database.configureDatabases
import com.kastack.vidyanet.services.notification.NotificationService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module(isTest: Boolean = false) {
    NotificationService.init()
    configureSerialization()
    configureSecurity()
    configureDatabases(isTest)
    configureHTTP()
    configureStatusPages()
    install(MaintenanceModePlugin)
    configureRouting()
}
