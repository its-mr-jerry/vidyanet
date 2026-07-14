package com.kastack.vidyanet.services.school

import com.kastack.vidyanet.models.user.UserType
import com.kastack.vidyanet.permissions.PermissionSchema
import com.kastack.vidyanet.plugins.authorize
import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.*

abstract class SchoolRules {
    abstract suspend fun createSchool(call: ApplicationCall)
    abstract suspend fun getAllSchools(call: ApplicationCall)
    abstract suspend fun getSchool(call: ApplicationCall)
    abstract suspend fun updateSchool(call: ApplicationCall)
    abstract suspend fun deleteSchool(call: ApplicationCall)

    fun Route.schoolRoutes() {
        route("/schools") {
            authorize(UserType.PLATFORM_OWNER) {
                post {
                    createSchool(call)
                }
                get {
                    getAllSchools(call)
                }
                delete("/{schoolId}") {
                    deleteSchool(call)
                }
            }

            authorize(UserType.PLATFORM_OWNER, UserType.SCHOOL_USER, permission = PermissionSchema.Settings.VIEW) {
                get("/{schoolId}") {
                    getSchool(call)
                }
            }
            authorize(UserType.PLATFORM_OWNER, UserType.SCHOOL_USER, permission = PermissionSchema.Settings.EDIT) {
                put("/{schoolId}") {
                    updateSchool(call)
                }
            }
        }
    }
}
