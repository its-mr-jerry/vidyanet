package com.kastack.vidyanet.services.role

import com.kastack.vidyanet.models.user.UserType
import com.kastack.vidyanet.plugins.authorize
import io.ktor.server.application.*
import io.ktor.server.routing.*

abstract class RoleRules {
    abstract suspend fun getAllRoles(call: ApplicationCall)
    abstract suspend fun getRoleById(call: ApplicationCall)
    abstract suspend fun createRole(call: ApplicationCall)
    abstract suspend fun updateRole(call: ApplicationCall)
    abstract suspend fun deleteRole(call: ApplicationCall)
    abstract suspend fun assignRole(call: ApplicationCall)
    abstract suspend fun revokeRole(call: ApplicationCall)
    abstract suspend fun getUserRoles(call: ApplicationCall)
    abstract suspend fun getRolePermissions(call: ApplicationCall)
    abstract suspend fun updateRolePermissions(call: ApplicationCall)
    abstract suspend fun getAllPermissions(call: ApplicationCall)

    fun Route.roleRoutes() {
        route("/roles") {
            authorize(UserType.PLATFORM_OWNER, UserType.SCHOOL_USER) {
                get { getAllRoles(call) }
                get("/{id}") { getRoleById(call) }
                post { createRole(call) }
                put("/{id}") { updateRole(call) }
                delete("/{id}") { deleteRole(call) }
                
                route("/{id}/permissions") {
                    get { getRolePermissions(call) }
                    put { updateRolePermissions(call) }
                }
            }
        }
        route("/permissions") {
            authorize(UserType.PLATFORM_OWNER, UserType.SCHOOL_USER) {
                get { getAllPermissions(call) }
            }
        }
        route("/user-roles") {
            authorize(UserType.PLATFORM_OWNER, UserType.SCHOOL_USER) {
                post("/assign") { assignRole(call) }
                delete("/revoke") { revokeRole(call) }
                get("/{userId}") { getUserRoles(call) }
            }
        }
    }
}
