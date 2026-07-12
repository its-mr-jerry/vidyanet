package com.kastack.vidyanet.services.schoolSettings

import com.kastack.vidyanet.models.schoolUser.UpdateSchoolSettingsRequest
import com.kastack.vidyanet.plugins.ensureSchoolAccess
import com.kastack.vidyanet.validators.SchoolSettingsValidator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class SchoolSettingsController(
    private val settingsService: SchoolSettingsService = SchoolSettingsService()
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
        call.respond(settingsService.updateSchoolSettings(schoolId, request))
    }
}
