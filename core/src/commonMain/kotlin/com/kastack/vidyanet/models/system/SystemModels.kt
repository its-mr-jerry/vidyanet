package com.kastack.vidyanet.models.system

import kotlinx.serialization.Serializable

@Serializable
data class SystemConfigDto(
    val isMaintenanceMode: Boolean,
    val supportPhone: String,
    val supportEmail: String,
    val appVersion: String,
    val allowNewSchoolRegistration: Boolean
)

@Serializable
data class UpdateSystemConfigRequest(
    val isMaintenanceMode: Boolean? = null,
    val supportPhone: String? = null,
    val supportEmail: String? = null,
    val appVersion: String? = null,
    val allowNewSchoolRegistration: Boolean? = null
)
