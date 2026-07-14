package com.kastack.vidyanet.models.role

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class RoleDto(
    val id: Long,
    val roleCode: String,
    val roleName: String,
    val description: String? = null,
    val schoolId: Long? = null,
    val isSystemRole: Boolean = true,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
data class UserRoleDto(
    val id: Long,
    val userId: Long,
    val roleId: Long,
    val roleCode: String? = null,
    val roleName: String? = null,
    val assignedBy: Long? = null,
    val assignedAt: Instant
)

@Serializable
data class CreateRoleRequest(
    val roleCode: String,
    val roleName: String,
    val description: String? = null,
    val schoolId: Long? = null,
    val isSystemRole: Boolean = false
)

@Serializable
data class UpdateRoleRequest(
    val roleName: String? = null,
    val description: String? = null
)

@Serializable
data class AssignRoleRequest(
    val userId: Long,
    val roleId: Long
)
