package com.kastack.vidyanet.services.academicSettings

import com.kastack.vidyanet.models.schoolUser.AcademicSessionDto
import com.kastack.vidyanet.models.schoolUser.HolidayDto
import com.kastack.vidyanet.models.schoolUser.UpdateAcademicSettingsRequest
import com.kastack.vidyanet.plugins.ensureSchoolAccess
import com.kastack.vidyanet.validators.AcademicSettingsValidator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class AcademicSettingsController(
    private val service: AcademicSettingsService = AcademicSettingsService()
) : AcademicSettingsRules() {

    override suspend fun getSettings(call: ApplicationCall) {
        val schoolId = call.parameters["schoolId"]?.toLongOrNull()
        if (schoolId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing or invalid schoolId")
            return
        }
        call.ensureSchoolAccess(schoolId)
        val settings = service.getAcademicSettings(schoolId)
        call.respond(settings)
    }

    override suspend fun updateSettings(call: ApplicationCall) {
        val schoolId = call.parameters["schoolId"]?.toLongOrNull()
        if (schoolId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing or invalid schoolId")
            return
        }
        call.ensureSchoolAccess(schoolId)
        val request = call.receive<UpdateAcademicSettingsRequest>()
        AcademicSettingsValidator.validateUpdate(request)
        service.updateAcademicSettings(schoolId, request)
        call.respond(HttpStatusCode.OK, "Settings updated")
    }

    override suspend fun addSession(call: ApplicationCall) {
        val schoolId = call.parameters["schoolId"]?.toLongOrNull()
        if (schoolId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing or invalid schoolId")
            return
        }
        call.ensureSchoolAccess(schoolId)
        val request = call.receive<AcademicSessionDto>()
        AcademicSettingsValidator.validateSession(request)
        service.addAcademicSession(schoolId, request)
        call.respond(HttpStatusCode.Created, "Session added")
    }

    override suspend fun deleteSession(call: ApplicationCall) {
        val sessionId = call.parameters["sessionId"]?.toLongOrNull()
        if (sessionId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing or invalid sessionId")
            return
        }
        // Ideally we should check if this session belongs to a school the user has access to
        service.deleteAcademicSession(sessionId)
        call.respond(HttpStatusCode.OK, "Session deleted")
    }

    override suspend fun addHoliday(call: ApplicationCall) {
        val schoolId = call.parameters["schoolId"]?.toLongOrNull()
        if (schoolId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing or invalid schoolId")
            return
        }
        call.ensureSchoolAccess(schoolId)
        val request = call.receive<HolidayDto>()
        AcademicSettingsValidator.validateHoliday(request)
        service.addHoliday(schoolId, request)
        call.respond(HttpStatusCode.Created, "Holiday added")
    }

    override suspend fun deleteHoliday(call: ApplicationCall) {
        val holidayId = call.parameters["holidayId"]?.toLongOrNull()
        if (holidayId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing or invalid holidayId")
            return
        }
        service.deleteHoliday(holidayId)
        call.respond(HttpStatusCode.OK, "Holiday deleted")
    }
}
