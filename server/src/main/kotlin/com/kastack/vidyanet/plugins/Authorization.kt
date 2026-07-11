package com.kastack.vidyanet.plugins

import com.kastack.vidyanet.models.user.UserType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class AuthorizationConfig {
    var userTypes: Array<out UserType> = emptyArray()
    var roles: Array<out String> = emptyArray()
}

val RoleBasedAuthorization = createRouteScopedPlugin(
    name = "RoleBasedAuthorization",
    createConfiguration = ::AuthorizationConfig
) {
    val userTypes = pluginConfig.userTypes
    val roles = pluginConfig.roles

    on(AuthenticationChecked) { call ->
        if (userTypes.isEmpty() && roles.isEmpty()) return@on

        val principal = call.principal<JWTPrincipal>()
        val userType = principal?.payload?.getClaim("userType")?.asString()
        val userRoles = principal?.payload?.getClaim("roles")?.asList(String::class.java) ?: emptyList()
        
        val userId = try {
            principal?.payload?.getClaim("userId")?.asLong()
        } catch (e: Exception) {
            principal?.payload?.getClaim("userId")?.asInt()?.toLong()
        }

        val hasUserType = userTypes.isNotEmpty() && userType != null && userTypes.any { it.name == userType }
        val hasRole = roles.isNotEmpty() && roles.any { it in userRoles }

        if (!hasUserType && !hasRole) {
            println("Forbidden: User $userId with type '$userType' and roles $userRoles tried to access a route requiring types ${userTypes.joinToString { it.name }} or roles ${roles.joinToString()}")
            call.respond(HttpStatusCode.Forbidden, mapOf("message" to "You do not have permission to access this resource"))
        }
    }
}

fun Route.authorize(vararg userTypes: UserType, roles: Array<out String> = emptyArray(), build: Route.() -> Unit): Route {
    val authorizedRoute = createChild(object : RouteSelector() {
        override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
            RouteSelectorEvaluation.Constant
    })

    authorizedRoute.install(RoleBasedAuthorization) {
        this.userTypes = userTypes
        this.roles = roles
    }

    authorizedRoute.build()
    return authorizedRoute
}

val ApplicationCall.userId: Long?
    get() = try {
        principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
    } catch (e: Exception) {
        principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()?.toLong()
    }

val ApplicationCall.userType: String?
    get() = principal<JWTPrincipal>()?.payload?.getClaim("userType")?.asString()

val ApplicationCall.schoolId: Long?
    get() = try {
        principal<JWTPrincipal>()?.payload?.getClaim("schoolId")?.asLong()
    } catch (e: Exception) {
        principal<JWTPrincipal>()?.payload?.getClaim("schoolId")?.asInt()?.toLong()
    }

suspend fun ApplicationCall.ensureSchoolAccess(requestedSchoolId: Long) {
    val type = userType
    val userSchoolId = schoolId
    
    if (type == UserType.PLATFORM_OWNER.name) return
    
    if (type == UserType.SCHOOL_USER.name && userSchoolId == requestedSchoolId) return
    
    respond(HttpStatusCode.Forbidden, mapOf("message" to "You do not have access to this school's data"))
    throw ForbiddenException("Access denied to school $requestedSchoolId")
}

class ForbiddenException(message: String) : RuntimeException(message)

fun Route.authorizeRoles(vararg roles: String, build: Route.() -> Unit): Route {
    return authorize(roles = roles, build = build)
}
