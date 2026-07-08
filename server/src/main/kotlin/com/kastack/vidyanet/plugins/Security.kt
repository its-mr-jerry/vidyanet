package com.kastack.vidyanet.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kastack.vidyanet.config.AppConfig

fun Application.configureSecurity() {
    
    val jwtAudience = AppConfig.jwtAudience
    val jwtDomain = AppConfig.jwtDomain
    val jwtRealm = AppConfig.jwtRealm
    val jwtSecret = AppConfig.authSecret

    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { _, _ ->
                call.respond(io.ktor.http.HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}

// Utility to generate token
fun generateToken(id: Long, phone: String, userType: String, roles: List<String>): String {
    val jwtAudience = AppConfig.jwtAudience
    val jwtDomain = AppConfig.jwtDomain
    val jwtSecret = AppConfig.authSecret
    
    val expiration = System.currentTimeMillis() + (3600000L * 24 * 7)
    return JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtDomain)
        .withClaim("userId", id)
        .withClaim("phone", phone)
        .withClaim("userType", userType)
        .withArrayClaim("roles", roles.toTypedArray())
        .withExpiresAt(java.util.Date(expiration))
        .sign(Algorithm.HMAC256(jwtSecret))
}
