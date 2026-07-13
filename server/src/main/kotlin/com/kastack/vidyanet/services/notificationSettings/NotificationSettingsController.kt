package com.kastack.vidyanet.services.notificationSettings

import com.kastack.vidyanet.models.audit.AuditStatus
import com.kastack.vidyanet.models.settings.UpdateNotificationSettingsRequest
import com.kastack.vidyanet.plugins.userId
import com.kastack.vidyanet.services.audit.AuditLogService
import com.kastack.vidyanet.validators.NotificationValidator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class NotificationSettingsController(
    private val service: NotificationSettingsService = NotificationSettingsService(),
    private val auditLogService: AuditLogService = AuditLogService()
) : NotificationSettingsRules() {

    override suspend fun getSettings(call: ApplicationCall) {
        val schoolId = call.parameters["schoolId"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid School ID")
        call.respond(service.getSettings(schoolId))
    }

    override suspend fun updateSettings(call: ApplicationCall) {
        val schoolId = call.parameters["schoolId"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid School ID")
        val request = call.receive<UpdateNotificationSettingsRequest>()
        NotificationValidator.validateUpdate(request)
        service.updateSettings(schoolId, request)
        
        auditLogService.logAction(
            schoolId = schoolId,
            userId = call.userId,
            action = "Updated Notification Settings",
            actionDetails = "Modified communication channels or templates",
            module = "SETTINGS",
            status = AuditStatus.SUCCESS,
            ipAddress = call.request.local.remoteAddress,
            userAgent = call.request.headers["User-Agent"]
        )

        call.respond(HttpStatusCode.OK, "Notification settings updated")
    }
}
