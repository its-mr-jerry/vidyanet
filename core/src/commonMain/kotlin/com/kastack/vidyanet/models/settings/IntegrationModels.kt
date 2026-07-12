package com.kastack.vidyanet.models.settings

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class IntegrationDto(
    val id: String,
    val name: String,
    val description: String,
    val category: IntegrationCategory,
    val isConnected: Boolean,
    val iconUrl: String? = null
)

@Serializable
enum class IntegrationCategory {
    COMMUNICATION, PAYMENTS, LMS, OTHER
}

@Serializable
data class ApiKeyDto(
    val id: String,
    val label: String,
    val createdAt: Instant,
    val lastUsedAt: Instant? = null,
    val permissions: String // e.g. "Read/Write"
)

@Serializable
data class CreateApiKeyRequest(
    val label: String,
    val permissions: String
)
