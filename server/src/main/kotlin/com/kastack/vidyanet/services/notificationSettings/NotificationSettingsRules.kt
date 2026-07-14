package com.kastack.vidyanet.services.notificationSettings

import com.kastack.vidyanet.models.user.UserType
import com.kastack.vidyanet.permissions.PermissionSchema
import com.kastack.vidyanet.plugins.authorize
import io.ktor.server.application.*
import io.ktor.server.routing.*

abstract class NotificationSettingsRules {
    abstract suspend fun getSettings(call: ApplicationCall)
    abstract suspend fun updateSettings(call: ApplicationCall)

    fun Route.notificationSettingsRoutes() {
        route("/schools/{schoolId}/notification-settings") {
            authorize(UserType.PLATFORM_OWNER, UserType.SCHOOL_USER, permission = PermissionSchema.Settings.VIEW) {
                get { getSettings(call) }
            }
            authorize(UserType.PLATFORM_OWNER, UserType.SCHOOL_USER, permission = PermissionSchema.Settings.EDIT) {
                put { updateSettings(call) }
            }
        }
    }
}
