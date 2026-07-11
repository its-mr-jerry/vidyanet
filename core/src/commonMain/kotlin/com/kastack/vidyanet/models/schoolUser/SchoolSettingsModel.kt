package com.kastack.vidyanet.models.schoolUser

import kotlinx.serialization.Serializable

@Serializable
data class SchoolSettingsDto(
    val schoolId: Long,
    val registrationNumber: String? = null,
    val motto: String? = null,
    val establishmentDate: String? = null,
    val affiliationBoard: String? = null,
    val primaryBrandColor: String? = null,
    val isMaintenanceMode: Boolean = false,
    val workingHours: List<WorkingHourDto> = emptyList(),
    val branches: List<SchoolBranchDto> = emptyList()
)

@Serializable
data class WorkingHourDto(
    val id: Long? = null,
    val dayOfWeek: String, // MONDAY, TUESDAY, etc.
    val openingTime: String?, // "08:00"
    val closingTime: String?, // "16:00"
    val isClosed: Boolean
)

@Serializable
data class SchoolBranchDto(
    val id: Long? = null,
    val name: String,
    val type: String, // HEADQUARTERS, ELEMENTARY, etc.
    val address: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String,
    val contactPerson: String,
    val phone: String,
    val email: String?,
    val status: String // ACTIVE, INACTIVE
)

@Serializable
data class UpdateSchoolSettingsRequest(
    val registrationNumber: String? = null,
    val motto: String? = null,
    val establishmentDate: String? = null,
    val affiliationBoard: String? = null,
    val primaryBrandColor: String? = null,
    val isMaintenanceMode: Boolean? = null,
    val workingHours: List<WorkingHourDto>? = null
)
