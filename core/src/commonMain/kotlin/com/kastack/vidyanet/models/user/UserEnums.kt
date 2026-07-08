package com.kastack.vidyanet.models.user

import kotlinx.serialization.Serializable

@Serializable
enum class UserType {
    PLATFORM_OWNER,
    SCHOOL_USER
}

@Serializable
enum class UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    DELETED,
    PENDING_VERIFICATION
}
