package com.kastack.vidyanet.models.user

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val phone: String,
    val userType: UserType,
    val schoolId: Long? = null,
    val status: UserStatus,
    val isPhoneVerified: Boolean,
    val roles: List<String> = emptyList(),
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
data class UpdateUserRequest(
    val userType: UserType? = null,
    val status: UserStatus? = null,
    val schoolId: Long? = null
)
