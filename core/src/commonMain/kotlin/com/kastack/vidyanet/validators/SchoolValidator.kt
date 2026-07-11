package com.kastack.vidyanet.validators

import com.kastack.vidyanet.models.schoolUser.CreateSchoolRequest
import com.kastack.vidyanet.models.schoolUser.UpdateSchoolRequest

object SchoolValidator {

    fun validateCreate(request: CreateSchoolRequest) {
        // schoolCode is now optional as it's generated on backend if blank
        if (request.schoolName.isBlank()) throw ValidationException("School name is required")
        if (request.phone.isBlank()) throw ValidationException("Phone number is required")
        if (request.address.isBlank()) throw ValidationException("Address is required")
        if (request.city.isBlank()) throw ValidationException("City is required")
        if (request.state.isBlank()) throw ValidationException("State is required")
        if (request.country.isBlank()) throw ValidationException("Country is required")
        if (request.postalCode.isBlank()) throw ValidationException("Postal code is required")
        
        request.email?.let { validateEmail(it) }
    }

    fun validateUpdate(request: UpdateSchoolRequest) {
        request.schoolName?.let { if (it.isBlank()) throw ValidationException("School name cannot be blank") }
        request.phone?.let { if (it.isBlank()) throw ValidationException("Phone number cannot be blank") }
        request.email?.let { validateEmail(it) }
        request.address?.let { if (it.isBlank()) throw ValidationException("Address cannot be blank") }
        request.city?.let { if (it.isBlank()) throw ValidationException("City cannot be blank") }
        request.state?.let { if (it.isBlank()) throw ValidationException("State cannot be blank") }
        request.country?.let { if (it.isBlank()) throw ValidationException("Country cannot be blank") }
        request.postalCode?.let { if (it.isBlank()) throw ValidationException("Postal code cannot be blank") }
    }

    private fun validateEmail(email: String) {
        if (email.isNotBlank() && !email.contains("@")) {
            throw ValidationException("Invalid email format")
        }
    }
}
