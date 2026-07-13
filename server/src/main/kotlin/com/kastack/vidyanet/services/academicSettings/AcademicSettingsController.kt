package com.kastack.vidyanet.services.academicSettings

import com.kastack.vidyanet.models.audit.AuditStatus
import com.kastack.vidyanet.models.schoolUser.AcademicSessionDto
import com.kastack.vidyanet.models.schoolUser.HolidayDto
import com.kastack.vidyanet.models.schoolUser.UpdateAcademicSettingsRequest
import com.kastack.vidyanet.plugins.ensureSchoolAccess
import com.kastack.vidyanet.plugins.userId
import com.kastack.vidyanet.services.audit.AuditLogService
import com.kastack.vidyanet.validators.AcademicSettingsValidator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class AcademicSettingsController(
    private val service: AcademicSettingsService = AcademicSettingsService(),
    private val auditLogService: AuditLogService = AuditLogService()
) : AcademicSettingsRules() {

    override suspend fun getSettings(call: ApplicationCall) {
        val schoolId = call.parameters["schoolId"]?.toLongOrNull()
        if (schoolId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing or invalid schoolId")
            return
        }
        val settings = service.getAcademicSettings(schoolId)
        call.respond(settings)
    }

    override suspend fun updateSettings(call: ApplicationCall) {
        val schoolId = call.parameters["schoolId"]?.toLongOrNull()
        if (schoolId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing or invalid schoolId")
            return
        }
        val request = call.receive<UpdateAcademicSettingsRequest>()
        AcademicSettingsValidator.validateUpdate(request)
        service.updateAcademicSettings(schoolId, request)
        
        auditLogService.logAction(
            schoolId = schoolId,
            userId = call.userId,
            action = "Updated Academic Settings",
            actionDetails = "Modified grading scale or promotion rules",
            module = "ACADEMICS",
            status = AuditStatus.SUCCESS,
            ipAddress = call.request.local.remoteAddress,
            userAgent = call.request.headers["User-Agent"]
        )

        call.respond(HttpStatusCode.OK, "Settings updated")
    }

    override suspend fun addSession(call: ApplicationCall) {
        val schoolId = call.parameters["schoolId"]?.toLongOrNull()
        if (schoolId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing or invalid schoolId")
            return
        }
        val request = call.receive<AcademicSessionDto>()
        AcademicSettingsValidator.validateSession(request)
        service.addAcademicSession(schoolId, request)

        auditLogService.logAction(
            schoolId = schoolId,
            userId = call.userId,
            action = "Added Academic Session",
            actionDetails = "Created new session: ${request.name}",
            module = "ACADEMICS",
            status = AuditStatus.SUCCESS,
            ipAddress = call.request.local.remoteAddress,
            userAgent = call.request.headers["User-Agent"]
        )

        call.respond(HttpStatusCode.Created, "Session added")
    }

    override suspend fun deleteSession(call: ApplicationCall) {
        val schoolId = call.parameters["schoolId"]?.toLongOrNull()
        val sessionId = call.parameters["sessionId"]?.toLongOrNull()
        if (schoolId == null || sessionId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing or invalid parameters")
            return
        }
        service.deleteAcademicSession(schoolId, sessionId)

        auditLogService.logAction(
            schoolId = schoolId,
            userId = call.userId,
            action = "Deleted Academic Session",
            actionDetails = "Removed session ID: $sessionId",
            module = "ACADEMICS",
            status = AuditStatus.SUCCESS,
            ipAddress = call.request.local.remoteAddress,
            userAgent = call.request.headers["User-Agent"]
        )

        call.respond(HttpStatusCode.OK, "Session deleted")
    }

    override suspend fun addHoliday(call: ApplicationCall) {
        val schoolId = call.parameters["schoolId"]?.toLongOrNull()
        if (schoolId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing or invalid schoolId")
            return
        }
        val request = call.receive<HolidayDto>()
        AcademicSettingsValidator.validateHoliday(request)
        service.addHoliday(schoolId, request)

        auditLogService.logAction(
            schoolId = schoolId,
            userId = call.userId,
            action = "Added Holiday",
            actionDetails = "Created holiday: ${request.name} on ${request.date}",
            module = "ACADEMICS",
            status = AuditStatus.SUCCESS,
            ipAddress = call.request.local.remoteAddress,
            userAgent = call.request.headers["User-Agent"]
        )

        call.respond(HttpStatusCode.Created, "Holiday added")
    }

    override suspend fun deleteHoliday(call: ApplicationCall) {
        val schoolId = call.parameters["schoolId"]?.toLongOrNull()
        val holidayId = call.parameters["holidayId"]?.toLongOrNull()
        if (schoolId == null || holidayId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing or invalid parameters")
            return
        }
        service.deleteHoliday(schoolId, holidayId)

        auditLogService.logAction(
            schoolId = schoolId,
            userId = call.userId,
            action = "Deleted Holiday",
            actionDetails = "Removed holiday ID: $holidayId",
            module = "ACADEMICS",
            status = AuditStatus.SUCCESS,
            ipAddress = call.request.local.remoteAddress,
            userAgent = call.request.headers["User-Agent"]
        )

        call.respond(HttpStatusCode.OK, "Holiday deleted")
    }
}
