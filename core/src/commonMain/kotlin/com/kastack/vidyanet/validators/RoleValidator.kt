package com.kastack.vidyanet.validators

import com.kastack.vidyanet.models.role.AssignRoleRequest
import com.kastack.vidyanet.models.role.CreateRoleRequest
import com.kastack.vidyanet.models.role.UpdateRoleRequest

object RoleValidator {

    fun validateCreate(request: CreateRoleRequest) {
        if (request.roleCode.isBlank()) throw ValidationException("Role code is required")
        if (request.roleName.isBlank()) throw ValidationException("Role name is required")
    }

    fun validateUpdate(request: UpdateRoleRequest) {
        request.roleName?.let { if (it.isBlank()) throw ValidationException("Role name cannot be blank") }
    }

    fun validateAssignment(request: AssignRoleRequest) {
        if (request.userId <= 0) throw ValidationException("Invalid user ID")
        if (request.roleId <= 0) throw ValidationException("Invalid role ID")
    }
}
