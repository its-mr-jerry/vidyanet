package com.kastack.vidyanet.database.entities

import com.kastack.vidyanet.database.tables.UserRoleAssignmentsTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserRoleAssignmentEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserRoleAssignmentEntity>(UserRoleAssignmentsTable)

    var userId by UserRoleAssignmentsTable.userId
    var roleId by UserRoleAssignmentsTable.roleId
    var assignedBy by UserRoleAssignmentsTable.assignedBy
    var assignedAt by UserRoleAssignmentsTable.assignedAt

    var user by UserEntity referencedOn UserRoleAssignmentsTable.userId
    var role by RoleEntity referencedOn UserRoleAssignmentsTable.roleId
}
