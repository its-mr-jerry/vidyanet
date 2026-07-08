package com.kastack.vidyanet.models.schoolUser

import kotlinx.serialization.Serializable

@Serializable
data class SchoolDto(
    val id: Long,
    val schoolCode: String,
    val schoolName: String,
    val schoolType: SchoolType,
    val phone: String,
    val email: String?,
    val website: String?,
    val address: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String,
    val logoUrl: String?,
    val status: SchoolStatus
)

@Serializable
data class CreateSchoolRequest(
    val schoolCode: String,
    val schoolName: String,
    val schoolType: SchoolType,
    val phone: String,
    val email: String? = null,
    val website: String? = null,
    val address: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String,
    val logoUrl: String? = null,
    val status: SchoolStatus = SchoolStatus.PENDING_APPROVAL
)

@Serializable
data class UpdateSchoolRequest(
    val schoolName: String? = null,
    val schoolType: SchoolType? = null,
    val phone: String? = null,
    val email: String? = null,
    val website: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val postalCode: String? = null,
    val logoUrl: String? = null,
    val status: SchoolStatus? = null
)
