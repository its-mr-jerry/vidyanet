package com.kastack.vidyanet.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object PermissionsTable : LongIdTable("permissions", "permission_id") {
    val moduleName = varchar("module_name", 50)
    val action = varchar("action", 20) // VIEW, CREATE, EDIT, DELETE, EXPORT
    val description = varchar("description", 255).nullable()

    init {
        uniqueIndex(moduleName, action)
    }
}

object RolePermissionsTable : LongIdTable("role_permissions", "id") {
    val roleId = reference("role_id", RolesTable)
    val permissionId = reference("permission_id", PermissionsTable)

    init {
        uniqueIndex(roleId, permissionId)
    }
}
