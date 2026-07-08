package com.kastack.vidyanet.models.schoolUser

import kotlinx.serialization.Serializable

@Serializable
enum class SchoolType {
    PUBLIC,
    PRIVATE,
    GOVERNMENT,
    INTERNATIONAL,
    COLLEGE,
    UNIVERSITY,
    TRAINING_INSTITUTE
}

@Serializable
enum class SchoolStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    PENDING_APPROVAL,
    CLOSED
}