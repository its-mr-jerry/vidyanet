package com.kastack.vidyanet.services.user

import com.kastack.vidyanet.models.user.UserType
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
        authorize(UserType.PLATFORM_OWNER, UserType.SCHOOL_USER) {
            get("/stats") {
                getUserStats(call)
            }
            get {
                getAllUsers(call)
            }
            post {
                createUser(call)
            }
            get("/{userId}") {
                getUserById(call)
            }
            put("/{userId}") {
                updateUser(call)
            }
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
