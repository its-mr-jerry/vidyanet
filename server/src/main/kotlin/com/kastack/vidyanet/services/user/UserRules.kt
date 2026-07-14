package com.kastack.vidyanet.services.user

import com.kastack.vidyanet.models.user.UserType
import com.kastack.vidyanet.permissions.PermissionSchema
import com.kastack.vidyanet.plugins.authorize
import io.ktor.server.application.*
import io.ktor.server.routing.*

abstract class UserRules {
    abstract suspend fun getUserStats(call: ApplicationCall)
    abstract suspend fun getAllUsers(call: ApplicationCall)
    abstract suspend fun getUserById(call: ApplicationCall)
    abstract suspend fun updateUser(call: ApplicationCall)
    abstract suspend fun deleteUser(call: ApplicationCall)
    abstract suspend fun createUser(call: ApplicationCall)
    abstract suspend fun getMe(call: ApplicationCall)
    abstract suspend fun updateFcmToken(call: ApplicationCall)

    fun Route.UserRoutes() {
        authorize(UserType.PLATFORM_OWNER, UserType.SCHOOL_USER, permission = PermissionSchema.Settings.VIEW) {
            get("/stats") {
                getUserStats(call)
            }
            get {
                getAllUsers(call)
            }
            get("/{userId}") {
                getUserById(call)
            }
        }

        authorize(UserType.PLATFORM_OWNER, UserType.SCHOOL_USER, permission = PermissionSchema.Settings.EDIT) {
            post {
                createUser(call)
            }
            put("/{userId}") {
                updateUser(call)
            }
        }

        authorize(UserType.PLATFORM_OWNER, UserType.SCHOOL_USER, permission = PermissionSchema.Settings.DELETE) {
            delete("/{userId}") {
                deleteUser(call)
            }
        }

        get("/me") {
            getMe(call)
        }
        put("/fcm-token") {
            updateFcmToken(call)
        }
    }
}
