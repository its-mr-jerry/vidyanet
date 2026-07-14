package com.kastack.vidyanet.database.entities

import com.kastack.vidyanet.database.tables.RolePermissionsTable
import com.kastack.vidyanet.database.tables.RolesTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class RoleEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<RoleEntity>(RolesTable)

    var roleCode by RolesTable.roleCode
    var roleName by RolesTable.roleName
    var description by RolesTable.description
    var schoolId by RolesTable.schoolId
    var isSystemRole by RolesTable.isSystemRole
    var createdAt by RolesTable.createdAt
    var updatedAt by RolesTable.updatedAt

    var permissions by PermissionEntity via RolePermissionsTable
}
