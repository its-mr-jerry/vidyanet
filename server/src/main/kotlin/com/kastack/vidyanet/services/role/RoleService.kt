package com.kastack.vidyanet.services.role

import com.kastack.vidyanet.database.entities.RoleEntity
import com.kastack.vidyanet.database.entities.UserRoleAssignmentEntity
import com.kastack.vidyanet.database.entities.toDto
import com.kastack.vidyanet.database.tables.RolesTable
import com.kastack.vidyanet.database.tables.UserRoleAssignmentsTable
import com.kastack.vidyanet.database.toKotlinx
import com.kastack.vidyanet.models.role.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Clock

class RoleService {

    fun getAllRoles(): List<RoleDto> = transaction {
        RoleEntity.all().orderBy(RolesTable.roleName to SortOrder.ASC).map { it.toDto() }
    }

    fun getRoleById(id: Long): RoleDto? = transaction {
        RoleEntity.findById(id)?.toDto()
    }

    fun createRole(request: CreateRoleRequest): RoleDto = transaction {
        RoleEntity.new {
            roleCode = request.roleCode
            roleName = request.roleName
            description = request.description
            isSystemRole = request.isSystemRole
            createdAt = Clock.System.now().toKotlinx()
            updatedAt = Clock.System.now().toKotlinx()
        }.toDto()
    }

    fun updateRole(id: Long, request: UpdateRoleRequest): RoleDto = transaction {
        val role = RoleEntity.findById(id) ?: throw IllegalArgumentException("Role not found")
        if (role.isSystemRole) throw IllegalArgumentException("System roles cannot be updated")

        request.roleName?.let { role.roleName = it }
        request.description?.let { role.description = it }
        role.updatedAt = Clock.System.now().toKotlinx()
        role.toDto()
    }

    fun deleteRole(id: Long): Boolean = transaction {
        val role = RoleEntity.findById(id) ?: return@transaction false
        if (role.isSystemRole) throw IllegalArgumentException("System roles cannot be deleted")
        role.delete()
        true
    }

    fun assignRole(request: AssignRoleRequest, assignedByUserId: Long): UserRoleDto = transaction {
        // Check if assignment already exists
        val existing = UserRoleAssignmentEntity.find {
            (UserRoleAssignmentsTable.userId eq request.userId) and (UserRoleAssignmentsTable.roleId eq request.roleId)
        }.firstOrNull()

        if (existing != null) return@transaction existing.toDto()

        UserRoleAssignmentEntity.new {
            userId = EntityID(request.userId, com.kastack.vidyanet.database.tables.UsersTable)
            roleId = EntityID(request.roleId, com.kastack.vidyanet.database.tables.RolesTable)
            assignedBy = EntityID(assignedByUserId, com.kastack.vidyanet.database.tables.UsersTable)
            assignedAt = Clock.System.now().toKotlinx()
        }.toDto()
    }

    fun revokeRole(userId: Long, roleId: Long): Boolean = transaction {
        val assignment = UserRoleAssignmentEntity.find {
            (UserRoleAssignmentsTable.userId eq userId) and (UserRoleAssignmentsTable.roleId eq roleId)
        }.firstOrNull() ?: return@transaction false

        assignment.delete()
        true
    }

    fun getUserRoles(userId: Long): List<UserRoleDto> = transaction {
        UserRoleAssignmentEntity.find { UserRoleAssignmentsTable.userId eq userId }.map { it.toDto() }
    }
}
