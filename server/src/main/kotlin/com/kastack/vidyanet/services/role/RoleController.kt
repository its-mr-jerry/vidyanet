package com.kastack.vidyanet.services.role

import com.kastack.vidyanet.models.audit.AuditStatus
import com.kastack.vidyanet.models.role.*
import com.kastack.vidyanet.models.user.UserType
import com.kastack.vidyanet.plugins.schoolId
import com.kastack.vidyanet.plugins.userId
import com.kastack.vidyanet.services.audit.AuditLogService
import com.kastack.vidyanet.services.user.UserService
import com.kastack.vidyanet.validators.RoleValidator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class RoleController(
    private val roleService: RoleService = RoleService(),
    private val userService: UserService = UserService(),
    private val auditLogService: AuditLogService = AuditLogService()
) : RoleRules() {

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
        val role = roleService.createRole(request)
        
        auditLogService.logAction(
            schoolId = call.schoolId,
            userId = call.userId,
            action = "Created Role",
            actionDetails = "Created new role: ${request.roleName} (${request.roleCode})",
            module = "SETTINGS",
            status = AuditStatus.SUCCESS,
            ipAddress = call.request.local.remoteAddress,
            userAgent = call.request.headers["User-Agent"]
        )

        call.respond(HttpStatusCode.Created, role)
    }

    override suspend fun updateRole(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        val request = call.receive<UpdateRoleRequest>()
        RoleValidator.validateUpdate(request)
        try {
            val role = roleService.updateRole(id, request)

            auditLogService.logAction(
                schoolId = call.schoolId,
                userId = call.userId,
                action = "Updated Role",
                actionDetails = "Modified role: ${request.roleName ?: id}",
                module = "SETTINGS",
                status = AuditStatus.SUCCESS,
                ipAddress = call.request.local.remoteAddress,
                userAgent = call.request.headers["User-Agent"]
            )

            call.respond(role)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Update failed")
        }
    }

    override suspend fun deleteRole(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        try {
            if (roleService.deleteRole(id)) {
                auditLogService.logAction(
                    schoolId = call.schoolId,
                    userId = call.userId,
                    action = "Deleted Role",
                    actionDetails = "Removed role ID: $id",
                    module = "SETTINGS",
                    status = AuditStatus.SUCCESS,
                    ipAddress = call.request.local.remoteAddress,
                    userAgent = call.request.headers["User-Agent"]
                )
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

        val result = roleService.assignRole(request, assignedByUserId)

        auditLogService.logAction(
            schoolId = call.schoolId,
            userId = assignedByUserId,
            action = "Assigned Role",
            actionDetails = "Assigned role ID ${request.roleId} to user ID ${request.userId}",
            module = "STAFF",
            status = AuditStatus.SUCCESS,
            ipAddress = call.request.local.remoteAddress,
            userAgent = call.request.headers["User-Agent"]
        )

        call.respond(HttpStatusCode.Created, result)
    }

    override suspend fun revokeRole(call: ApplicationCall) {
        val userId = call.request.queryParameters["userId"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid User ID")
        val roleId = call.request.queryParameters["roleId"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid Role ID")
        
        if (roleService.revokeRole(userId, roleId)) {
            auditLogService.logAction(
                schoolId = call.schoolId,
                userId = call.userId,
                action = "Revoked Role",
                actionDetails = "Revoked role ID $roleId from user ID $userId",
                module = "STAFF",
                status = AuditStatus.SUCCESS,
                ipAddress = call.request.local.remoteAddress,
                userAgent = call.request.headers["User-Agent"]
            )
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    override suspend fun getUserRoles(call: ApplicationCall) {
        val userId = call.parameters["userId"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid User ID")
        call.respond(roleService.getUserRoles(userId))
    }

    override suspend fun getRolePermissions(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        try {
            call.respond(roleService.getRolePermissions(id))
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.NotFound, e.message ?: "Role not found")
        }
    }

    override suspend fun updateRolePermissions(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull() ?: return call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        val request = call.receive<RolePermissionsDto>()
        try {
            roleService.updateRolePermissions(id, request)

            auditLogService.logAction(
                schoolId = call.schoolId, // Log against current user's school
                userId = call.userId,
                action = "Updated Role Permissions",
                actionDetails = "Modified permissions for role ID: $id",
                module = "SETTINGS",
                status = AuditStatus.SUCCESS,
                ipAddress = call.request.local.remoteAddress,
                userAgent = call.request.headers["User-Agent"]
            )

            call.respond(HttpStatusCode.OK, "Permissions updated")
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Update failed")
        }
    }

    override suspend fun getAllPermissions(call: ApplicationCall) {
        call.respond(roleService.getAllPermissions())
    }
}

