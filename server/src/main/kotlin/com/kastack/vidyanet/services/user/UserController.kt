package com.kastack.vidyanet.services.user

import com.kastack.vidyanet.models.user.CreateUserRequest
import com.kastack.vidyanet.models.user.UpdateUserRequest
import com.kastack.vidyanet.models.user.UserStatus
import com.kastack.vidyanet.models.user.UserType
import com.kastack.vidyanet.validators.UserValidator
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal

class UserController(private val userService: UserService = UserService()) : UserRules() {

    override suspend fun getAllUsers(call: ApplicationCall) {
        val search = call.request.queryParameters["search"]
        val userType = call.request.queryParameters["userType"]?.let { UserType.valueOf(it) }
        val status = call.request.queryParameters["status"]?.let { UserStatus.valueOf(it) }
        val roleId = call.request.queryParameters["roleId"]?.toLongOrNull()
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 10
        
        // If current user is SCHOOL_USER, restrict to their school
        val principal = call.principal<JWTPrincipal>()
        val currentUserType = principal?.payload?.getClaim("userType")?.asString()?.let { UserType.valueOf(it) }
        val schoolId = if (currentUserType == UserType.SCHOOL_USER) {
            principal.payload?.getClaim("schoolId")?.asLong()
        } else {
            call.request.queryParameters["schoolId"]?.toLongOrNull()
        }

        call.respond(userService.getAllUsers(search, userType, status, roleId, schoolId, page, pageSize))
    }

    override suspend fun getUserById(call: ApplicationCall) {
        val id = call.parameters["userId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid ID"))
        
        val user = userService.getUser(id)
        call.respond(user)
    }

    override suspend fun getUserStats(call: ApplicationCall) {
        call.respond(userService.getUserStats())
    }

    override suspend fun createUser(call: ApplicationCall) {
        val request = call.receive<CreateUserRequest>()
        UserValidator.validateCreate(request)
        
        val principal = call.principal<JWTPrincipal>()
        val currentUserType = principal?.payload?.getClaim("userType")?.asString()
        val currentSchoolId = principal?.payload?.getClaim("schoolId")?.asLong()
        
        val finalRequest = if (currentUserType == UserType.SCHOOL_USER.name) {
            request.copy(schoolId = currentSchoolId)
        } else {
            request
        }
        
        call.respond(HttpStatusCode.Created, userService.createUser(finalRequest))
    }

    override suspend fun updateUser(call: ApplicationCall) {
        val id = call.parameters["userId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid ID"))
        
        val request = call.receive<UpdateUserRequest>()
        UserValidator.validateUpdate(request)
        
        val principal = call.principal<JWTPrincipal>()
        val currentUserType = principal?.payload?.getClaim("userType")?.asString()
        val currentSchoolId = principal?.payload?.getClaim("schoolId")?.asLong()

        // Prevent SCHOOL_USER from escalating privileges
        if (currentUserType == UserType.SCHOOL_USER.name) {
            if (request.userType == UserType.PLATFORM_OWNER) {
                return call.respond(HttpStatusCode.Forbidden, mapOf("message" to "Cannot change user type to Platform Owner"))
            }
            if (request.schoolId != null && request.schoolId != currentSchoolId) {
                return call.respond(HttpStatusCode.Forbidden, mapOf("message" to "Cannot move user to another school"))
            }
        }

        call.respond(userService.updateUser(id, request))
    }

    override suspend fun deleteUser(call: ApplicationCall) {
        val id = call.parameters["userId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid ID"))
        
        userService.deleteUser(id)
        call.respond(HttpStatusCode.NoContent)
    }
    
    override suspend fun getMe(call: ApplicationCall) {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.payload?.getClaim("userId")?.asLong() ?: throw Exception("Invalid Token")
        call.respond(userService.getMe(userId))
    }
}

