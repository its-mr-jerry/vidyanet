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
    var permission: String? = null
}

val RoleBasedAuthorization = createRouteScopedPlugin(
    name = "RoleBasedAuthorization",
    createConfiguration = ::AuthorizationConfig
) {
    val userTypes = pluginConfig.userTypes
    val roles = pluginConfig.roles
    val requiredPermission = pluginConfig.permission

    on(AuthenticationChecked) { call ->
        val principal = call.principal<JWTPrincipal>() ?: return@on
        val userType = principal.payload.getClaim("userType").asString()
        val userRoles = principal.payload.getClaim("roles").asList(String::class.java) ?: emptyList()
        
        val isPlatformOwner = userType == UserType.PLATFORM_OWNER.name

        // 1. Role/Type Check
        if (userTypes.isNotEmpty() || roles.isNotEmpty()) {
            val hasUserType = userTypes.isEmpty() || userTypes.any { it.name == userType }
            val hasRole = roles.isEmpty() || roles.any { it in userRoles }

            // Platform Owners are automatically authorized for any role-gated route
            if (!hasUserType && !hasRole && !isPlatformOwner) {
                call.respond(HttpStatusCode.Forbidden, mapOf("message" to "Insufficient permissions"))
                return@on
            }
        }

        // 2. Ownership Check (Multi-tenancy isolation)
        // Platform Owners can bypass school-level and user-level restrictions
        if (!isPlatformOwner) {
            // Check School Access if schoolId is in path
            call.parameters["schoolId"]?.toLongOrNull()?.let { pathSchoolId ->
                val schoolIdClaim = try {
                    principal.payload.getClaim("schoolId").asLong()
                } catch (e: Exception) {
                    principal.payload.getClaim("schoolId").asInt()?.toLong()
                }

                if (schoolIdClaim != pathSchoolId) {
                    call.respond(HttpStatusCode.Forbidden, mapOf("message" to "Access denied: This data belongs to another school"))
                    return@on
                }
            }

            // Check User Access if userId is in path
            call.parameters["userId"]?.toLongOrNull()?.let { pathUserId ->
                val userIdClaim = try {
                    principal.payload.getClaim("userId").asLong()
                } catch (e: Exception) {
                    principal.payload.getClaim("userId").asInt()?.toLong()
                }

                if (userIdClaim != pathUserId) {
                    // School Admins can access their own school's users
                    val isSchoolAdmin = userRoles.contains("SCHOOL_ADMIN")
                    if (!isSchoolAdmin) {
                        call.respond(HttpStatusCode.Forbidden, mapOf("message" to "Access denied to user data"))
                        return@on
                    }
                }
            }
        }

        // 3. Granular Permission Check
        if (requiredPermission != null && !isPlatformOwner) {
            val userIdClaim = try {
                principal.payload.getClaim("userId").asLong()
            } catch (e: Exception) {
                principal.payload.getClaim("userId").asInt()?.toLong()
            }

            val hasPermission = checkUserPermission(userIdClaim!!, requiredPermission)
            if (!hasPermission) {
                call.respond(HttpStatusCode.Forbidden, mapOf("message" to "You do not have the required permission: $requiredPermission"))
                return@on
            }
        }
    }
}

// Simple permission check (In production, use a cache)
private fun checkUserPermission(userId: Long, permissionName: String): Boolean {
    // Permission format is MODULE_ACTION (e.g. STUDENTS_VIEW)
    val parts = permissionName.split("_")
    if (parts.size != 2) return false
    val module = parts[0]
    val action = parts[1]

    return org.jetbrains.exposed.sql.transactions.transaction {
        val user = com.kastack.vidyanet.database.entities.UserEntity.findById(userId) ?: return@transaction false
        
        // Platform Owner bypass
        if (user.userType == UserType.PLATFORM_OWNER) return@transaction true
        
        // Check if any role has this permission
        user.roles.any { role ->
            role.permissions.any { perm -> 
                perm.moduleName == module && perm.action == action 
            }
        }
    }
}

fun Route.authorize(vararg userTypes: UserType, roles: Array<out String> = emptyArray(), permission: String? = null, build: Route.() -> Unit): Route {
    val authorizedRoute = createChild(object : RouteSelector() {
        override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
            RouteSelectorEvaluation.Constant
    })

    authorizedRoute.install(RoleBasedAuthorization) {
        this.userTypes = userTypes
        this.roles = roles
        this.permission = permission
    }

    authorizedRoute.build()
    return authorizedRoute
}

fun Route.requirePermission(permission: String, build: Route.() -> Unit): Route {
    return authorize(permission = permission, build = build)
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

suspend fun ApplicationCall.ensureUserAccess(targetUserId: Long) {
    val type = userType
    val currentUserId = userId
    
    if (type == UserType.PLATFORM_OWNER.name) return
    
    if (currentUserId == targetUserId) return
    
    respond(HttpStatusCode.Forbidden, mapOf("message" to "You do not have permission to access this user's data"))
    throw ForbiddenException("Access denied to user $targetUserId")
}

class ForbiddenException(message: String) : RuntimeException(message)

fun Route.authorizeRoles(vararg roles: String, build: Route.() -> Unit): Route {
    return authorize(roles = roles, build = build)
}
