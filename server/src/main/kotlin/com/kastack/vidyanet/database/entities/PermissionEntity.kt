package com.kastack.vidyanet.database.entities

import com.kastack.vidyanet.database.tables.PermissionsTable
import com.kastack.vidyanet.database.tables.RolePermissionsTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PermissionEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PermissionEntity>(PermissionsTable)

    var moduleName by PermissionsTable.moduleName
    var action by PermissionsTable.action
    var description by PermissionsTable.description
}
