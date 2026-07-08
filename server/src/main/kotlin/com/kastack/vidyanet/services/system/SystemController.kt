package com.kastack.vidyanet.services.system

import com.kastack.vidyanet.models.system.UpdateSystemConfigRequest
import com.kastack.vidyanet.validators.SystemValidator
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

class SystemController(private val systemService: SystemService = SystemService()) : SystemRules() {

    override suspend fun getSystemConfig(call: ApplicationCall) {
        call.respond(systemService.getSystemConfig())
    }

    override suspend fun updateSystemConfig(call: ApplicationCall) {
        val request = call.receive<UpdateSystemConfigRequest>()
        SystemValidator.validateUpdate(request)
        call.respond(systemService.updateSystemConfig(request))
    }
}
