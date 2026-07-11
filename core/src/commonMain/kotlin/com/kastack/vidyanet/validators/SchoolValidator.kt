package com.kastack.vidyanet.validators

import com.kastack.vidyanet.models.schoolUser.CreateSchoolRequest
import com.kastack.vidyanet.models.schoolUser.UpdateSchoolRequest

object SchoolValidator {

    fun validateCreate(request: CreateSchoolRequest) {
        // schoolCode is now optional as it's generated on backend if blank
        ValidationSchema.school.name.validate(request.schoolName)
        ValidationSchema.school.phone.validate(request.phone)
        ValidationSchema.school.address.validate(request.address)
        ValidationSchema.school.city.validate(request.city)
        ValidationSchema.school.state.validate(request.state)
        ValidationSchema.school.country.validate(request.country)
        ValidationSchema.school.postalCode.validate(request.postalCode)
        ValidationSchema.school.email.validate(request.email)
    }

    fun validateUpdate(request: UpdateSchoolRequest) {
        request.schoolName?.let { ValidationSchema.school.name.validate(it) }
        request.phone?.let { ValidationSchema.school.phone.validate(it) }
        request.email?.let { ValidationSchema.school.email.validate(it) }
        request.address?.let { ValidationSchema.school.address.validate(it) }
        request.city?.let { ValidationSchema.school.city.validate(it) }
        request.state?.let { ValidationSchema.school.state.validate(it) }
        request.country?.let { ValidationSchema.school.country.validate(it) }
        request.postalCode?.let { ValidationSchema.school.postalCode.validate(it) }
    }

    private fun validateEmail(email: String) {
        if (email.isNotBlank() && !email.contains("@")) {
            throw ValidationException("Invalid email format")
        }
    }
}
