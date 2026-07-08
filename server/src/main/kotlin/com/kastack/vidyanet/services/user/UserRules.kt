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
    abstract suspend fun getMe(call: ApplicationCall)

    fun Route.UserRoutes() {
        authorize(UserType.PLATFORM_OWNER){
            get("/stats") {
                getUserStats(call)
            }
            get {
                getAllUsers(call)
            }
            get("/{id}") {
                getUserById(call)
            }
            put("/{id}") {
                updateUser(call)
            }
            delete("/{id}") {
                deleteUser(call)
            }
        }
        get("/me") {
            getMe(call)
        }

    }
}
