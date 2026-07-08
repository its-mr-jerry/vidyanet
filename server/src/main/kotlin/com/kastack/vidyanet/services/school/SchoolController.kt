package com.kastack.vidyanet.services.school

import com.kastack.vidyanet.models.schoolUser.CreateSchoolRequest
import com.kastack.vidyanet.models.schoolUser.UpdateSchoolRequest
import com.kastack.vidyanet.validators.SchoolValidator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class SchoolController(private val schoolService: SchoolService = SchoolService()) : SchoolRules() {

    override suspend fun createSchool(call: ApplicationCall) {
        val request = call.receive<CreateSchoolRequest>()
        SchoolValidator.validateCreate(request)
        call.respond(HttpStatusCode.Created, schoolService.createSchool(request))
    }

    override suspend fun getAllSchools(call: ApplicationCall) {
        call.respond(schoolService.getAllSchools())
    }

    override suspend fun getSchool(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return
        }
        val school = schoolService.getSchoolById(id)
        if (school != null) {
            call.respond(school)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    override suspend fun updateSchool(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return
        }
        val request = call.receive<UpdateSchoolRequest>()
        SchoolValidator.validateUpdate(request)
        try {
            call.respond(schoolService.updateSchool(id, request))
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.NotFound, e.message ?: "School not found")
        }
    }

    override suspend fun deleteSchool(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return
        }
        if (schoolService.deleteSchool(id)) {
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}
