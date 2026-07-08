package com.kastack.vidyanet.services.role

import com.kastack.vidyanet.models.role.AssignRoleRequest
import com.kastack.vidyanet.models.role.CreateRoleRequest
import com.kastack.vidyanet.models.role.UpdateRoleRequest
import com.kastack.vidyanet.validators.RoleValidator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class RoleController(private val roleService: RoleService = RoleService()) : RoleRules() {

    override suspend fun getAllRoles(call: ApplicationCall) {
        call.respond(roleService.getAllRoles())
    }

    override suspend fun getRoleById(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        val role = roleService.getRoleById(id) ?: return call.respond(HttpStatusCode.NotFound)
        call.respond(role)
    }

    override suspend fun createRole(call: ApplicationCall) {
        val request = call.receive<CreateRoleRequest>()
        RoleValidator.validateCreate(request)
        call.respond(HttpStatusCode.Created, roleService.createRole(request))
    }

    override suspend fun updateRole(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        val request = call.receive<UpdateRoleRequest>()
        RoleValidator.validateUpdate(request)
        try {
            call.respond(roleService.updateRole(id, request))
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Update failed")
        }
    }

    override suspend fun deleteRole(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        try {
            if (roleService.deleteRole(id)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Delete failed")
        }
    }

    override suspend fun assignRole(call: ApplicationCall) {
        val request = call.receive<AssignRoleRequest>()
        RoleValidator.validateAssignment(request)
        
        val principal = call.principal<JWTPrincipal>()
        val assignedByUserId = principal?.payload?.getClaim("userId")?.asLong() ?: throw Exception("Invalid Token")

        call.respond(HttpStatusCode.Created, roleService.assignRole(request, assignedByUserId))
    }

    override suspend fun revokeRole(call: ApplicationCall) {
        val userId = call.request.queryParameters["userId"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid User ID")
        val roleId = call.request.queryParameters["roleId"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid Role ID")
        
        if (roleService.revokeRole(userId, roleId)) {
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    override suspend fun getUserRoles(call: ApplicationCall) {
        val userId = call.parameters["userId"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid User ID")
        call.respond(roleService.getUserRoles(userId))
    }
}
