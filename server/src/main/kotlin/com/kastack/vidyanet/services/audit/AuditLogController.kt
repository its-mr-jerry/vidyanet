package com.kastack.vidyanet.services.audit

import com.kastack.vidyanet.models.audit.AuditStatus
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.http.*

class AuditLogController(private val service: AuditLogService = AuditLogService()) : AuditLogRules() {

    override suspend fun getAuditLogs(call: ApplicationCall) {
        val schoolId = call.parameters["schoolId"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid School ID")
        val search = call.request.queryParameters["search"]
        val module = call.request.queryParameters["module"]
        val status = call.request.queryParameters["status"]?.let { AuditStatus.valueOf(it) }
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 10

        call.respond(service.getAuditLogs(schoolId, search, module, status, page, pageSize))
    }
}
