package com.kastack.vidyanet.services.user

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
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 10
        
        call.respond(userService.getAllUsers(search, userType, status, page, pageSize))
    }

    override suspend fun getUserById(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid ID"))
        call.respond(userService.getUser(id))
    }

    override suspend fun getUserStats(call: ApplicationCall) {
        call.respond(userService.getUserStats())
    }

    override suspend fun updateUser(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid ID"))
        val request = call.receive<UpdateUserRequest>()
        UserValidator.validateUpdate(request)
        call.respond(userService.updateUser(id, request))
    }

    override suspend fun deleteUser(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull()
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
