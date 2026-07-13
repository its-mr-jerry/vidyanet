package com.kastack.vidyanet.database.entities

import com.kastack.vidyanet.database.tables.UsersTable
import com.kastack.vidyanet.database.tables.UserRoleAssignmentsTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(UsersTable)

    var phone by UsersTable.phone
    var fullName by UsersTable.fullName
    var email by UsersTable.email
    var userType by UsersTable.userType
    var status by UsersTable.status
    var schoolId by UsersTable.schoolId
    var isPhoneVerified by UsersTable.isPhoneVerified
    var fcmToken by UsersTable.fcmToken
    var lastLoginAt by UsersTable.lastLoginAt
    var createdAt by UsersTable.createdAt
    var updatedAt by UsersTable.updatedAt
    var deletedAt by UsersTable.deletedAt

    val roles by RoleEntity.via(UserRoleAssignmentsTable.userId, UserRoleAssignmentsTable.roleId)
}
