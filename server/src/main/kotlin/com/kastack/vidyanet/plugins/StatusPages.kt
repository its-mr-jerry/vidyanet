package com.kastack.vidyanet.plugins

import com.kastack.vidyanet.validators.ValidationException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import java.lang.reflect.InvocationTargetException

fun Application.configureStatusPages() {

    install(StatusPages) {

        exception<ValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("message" to cause.message)
            )
        }

        exception<UnauthorizedException> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                mapOf("message" to cause.message)
            )
        }

        exception<ForbiddenException> { call, cause ->
            call.respond(
                HttpStatusCode.Forbidden,
                mapOf("message" to cause.message)
            )
        }

        exception<NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                mapOf("message" to cause.message)
            )
        }

        exception<BadRequestException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("message" to cause.message)
            )
        }

        exception<InvocationTargetException> { call, cause ->
            val actualCause = cause.cause ?: cause
            call.application.environment.log.error("Wrapped exception: ${actualCause.message}", actualCause)
            // Rethrow or handle based on the actual cause if needed, 
            // but for now, we just want to see the real error in logs.
            throw actualCause
        }

        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception: ${cause.message}", cause)
            val message = cause.message ?: "An unexpected error occurred"
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("message" to message)
            )
        }
    }
}
