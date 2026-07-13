package com.kastack.vidyanet.routes

import com.kastack.vidyanet.services.audit.AuditLogController
import io.ktor.server.routing.*

fun Route.AuditLogRoute(controller: AuditLogController = AuditLogController()) {
    controller.run {
        auditLogRoutes()
    }
}
