package com.kastack.vidyanet.services.schoolSettings

import com.kastack.vidyanet.models.audit.AuditStatus
import com.kastack.vidyanet.models.schoolUser.UpdateSchoolSettingsRequest
import com.kastack.vidyanet.plugins.ensureSchoolAccess
import com.kastack.vidyanet.plugins.userId
import com.kastack.vidyanet.services.audit.AuditLogService
import com.kastack.vidyanet.validators.SchoolSettingsValidator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class SchoolSettingsController(
    private val settingsService: SchoolSettingsService = SchoolSettingsService(),
    private val auditLogService: AuditLogService = AuditLogService()
) : SchoolSettingsRules() {

    override suspend fun getSchoolSettings(call: ApplicationCall) {
        val schoolId = call.parameters["schoolId"]?.toLongOrNull()
        if (schoolId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid School ID")
            return
        }
        call.respond(settingsService.getSchoolSettings(schoolId))
    }

    override suspend fun updateSchoolSettings(call: ApplicationCall) {
        val schoolId = call.parameters["schoolId"]?.toLongOrNull()
        if (schoolId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid School ID")
            return
        }
        val request = call.receive<UpdateSchoolSettingsRequest>()
        SchoolSettingsValidator.validateUpdate(request)
        val result = settingsService.updateSchoolSettings(schoolId, request)
        
        auditLogService.logAction(
            schoolId = schoolId,
            userId = call.userId,
            action = "Updated School Settings",
            actionDetails = "Updated institution profile and branding",
            module = "SETTINGS",
            status = AuditStatus.SUCCESS,
            ipAddress = call.request.local.remoteAddress,
            userAgent = call.request.headers["User-Agent"]
        )
        
        call.respond(result)
    }
}
