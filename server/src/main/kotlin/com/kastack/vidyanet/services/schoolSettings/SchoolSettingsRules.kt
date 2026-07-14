package com.kastack.vidyanet.services.schoolSettings

import com.kastack.vidyanet.models.user.UserType
import com.kastack.vidyanet.permissions.PermissionSchema
import com.kastack.vidyanet.plugins.authorize
import io.ktor.server.application.*
import io.ktor.server.routing.*

abstract class SchoolSettingsRules {
    abstract suspend fun getSchoolSettings(call: ApplicationCall)
    abstract suspend fun updateSchoolSettings(call: ApplicationCall)

    fun Route.schoolSettingsRoutes() {
        route("/schools/{schoolId}/settings") {
            authorize(UserType.PLATFORM_OWNER, UserType.SCHOOL_USER, permission = PermissionSchema.Settings.VIEW) {
                get {
                    getSchoolSettings(call)
                }
            }
            authorize(UserType.PLATFORM_OWNER, UserType.SCHOOL_USER, permission = PermissionSchema.Settings.EDIT) {
                put {
                    updateSchoolSettings(call)
                }
            }
        }
    }
}
