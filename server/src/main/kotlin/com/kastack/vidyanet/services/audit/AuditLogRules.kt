package com.kastack.vidyanet.services.audit

import com.kastack.vidyanet.models.user.UserType
import com.kastack.vidyanet.plugins.authorize
import io.ktor.server.application.*
import io.ktor.server.routing.*

abstract class AuditLogRules {
    abstract suspend fun getAuditLogs(call: ApplicationCall)

    fun Route.auditLogRoutes() {
        route("/schools/{schoolId}/audit-logs") {
            authorize(UserType.PLATFORM_OWNER, UserType.SCHOOL_USER) {
                get { getAuditLogs(call) }
            }
        }
    }
}
