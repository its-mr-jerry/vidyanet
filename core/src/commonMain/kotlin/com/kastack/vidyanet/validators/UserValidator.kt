package com.kastack.vidyanet.validators

import com.kastack.vidyanet.models.user.CreateUserRequest
import com.kastack.vidyanet.models.user.UpdateUserRequest

object UserValidator {
    fun validateCreate(request: CreateUserRequest) {
        ValidationSchema.user.fullName.validate(request.fullName)
        ValidationSchema.user.phone.validate(request.phone)
        ValidationSchema.user.email.validate(request.email)
        
        if (request.roleIds.isEmpty()) {
            throw ValidationException("At least one role must be assigned")
        }
    }

    fun validateUpdate(request: UpdateUserRequest) {
        request.fullName?.let { ValidationSchema.user.fullName.validate(it) }
        request.email?.let { ValidationSchema.user.email.validate(it) }
        
        request.roleIds?.let {
            if (it.isEmpty()) {
                throw ValidationException("At least one role must be assigned")
            }
        }
    }
}
