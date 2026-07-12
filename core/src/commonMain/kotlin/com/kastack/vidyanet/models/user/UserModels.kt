package com.kastack.vidyanet.models.user

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val phone: String,
    val fullName: String? = null,
    val email: String? = null,
    val userType: UserType,
    val schoolId: Long? = null,
    val status: UserStatus,
    val isPhoneVerified: Boolean,
    val roles: List<String> = emptyList(),
    val permissions: List<String> = emptyList(), // Format: MODULE_ACTION
    val createdAt: Instant,
    val updatedAt: Instant,
    val lastLoginAt: Instant? = null,
    val deletedAt: Instant? = null
)

@Serializable
data class UserStatsDto(
    val totalUsers: Long,
    val newUsersToday: Long,
    val usersByType: Map<String, Long>
)

@Serializable
data class CreateUserRequest(
    val phone: String,
    val fullName: String,
    val email: String? = null,
    val userType: UserType,
    val schoolId: Long? = null,
    val roleIds: List<Long> = emptyList()
)

@Serializable
data class UpdateUserRequest(
    val fullName: String? = null,
    val email: String? = null,
    val userType: UserType? = null,
    val status: UserStatus? = null,
    val schoolId: Long? = null,
    val roleIds: List<Long>? = null
)

