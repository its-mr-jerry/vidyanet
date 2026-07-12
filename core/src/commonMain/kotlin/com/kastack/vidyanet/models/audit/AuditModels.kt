package com.kastack.vidyanet.models.audit

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class AuditStatus {
    INFO, WARNING, CRITICAL, SUCCESS, FAILURE
}

@Serializable
data class AuditLogDto(
    val id: Long,
    val timestamp: Instant,
    val userId: Long,
    val userName: String,
    val userRole: String,
    val action: String,
    val actionDetails: String? = null,
    val module: String,
    val status: AuditStatus,
    val ipAddress: String? = null,
    val userAgent: String? = null
)
