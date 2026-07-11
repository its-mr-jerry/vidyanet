package com.kastack.vidyanet.services.academicSettings

import com.kastack.vidyanet.models.user.UserType
import com.kastack.vidyanet.plugins.authorize
import io.ktor.server.application.*
import io.ktor.server.routing.*

abstract class AcademicSettingsRules {
    abstract suspend fun getSettings(call: ApplicationCall)
    abstract suspend fun updateSettings(call: ApplicationCall)
    abstract suspend fun addSession(call: ApplicationCall)
    abstract suspend fun deleteSession(call: ApplicationCall)
    abstract suspend fun addHoliday(call: ApplicationCall)
    abstract suspend fun deleteHoliday(call: ApplicationCall)

    fun Route.academicSettingsRoutes() {
        route("/schools/{schoolId}/academic-settings") {
            authorize(UserType.PLATFORM_OWNER, UserType.SCHOOL_USER) {
                get { getSettings(call) }
                put { updateSettings(call) }
                
                route("/sessions") {
                    post { addSession(call) }
                    delete("/{sessionId}") { deleteSession(call) }
                }

                route("/holidays") {
                    post { addHoliday(call) }
                    delete("/{holidayId}") { deleteHoliday(call) }
                }
            }
        }
    }
}
