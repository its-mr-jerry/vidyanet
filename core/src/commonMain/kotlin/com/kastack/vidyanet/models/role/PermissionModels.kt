package com.kastack.vidyanet.models.role

import kotlinx.serialization.Serializable

@Serializable
enum class PermissionAction {
    VIEW, CREATE, EDIT, DELETE, EXPORT
}

@Serializable
data class ModulePermissionDto(
    val moduleName: String,
    val description: String? = null,
    val actions: Set<PermissionAction>
)

@Serializable
data class RolePermissionsDto(
    val roleId: Long,
    val permissions: List<ModulePermissionDto>
)
