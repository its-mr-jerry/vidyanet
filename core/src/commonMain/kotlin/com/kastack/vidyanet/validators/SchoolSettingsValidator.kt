package com.kastack.vidyanet.validators

import com.kastack.vidyanet.models.schoolUser.UpdateSchoolSettingsRequest
import com.kastack.vidyanet.models.schoolUser.WorkingHourDto
import com.kastack.vidyanet.models.schoolUser.SchoolBranchDto

object SchoolSettingsValidator {

    fun validateUpdate(request: UpdateSchoolSettingsRequest) {
        request.primaryBrandColor?.let {
            if (!it.matches(Regex("^#([A-Fa-f0-8]{6}|[A-Fa-f0-8]{3})$"))) {
                throw ValidationException("Invalid primary brand color format. Expected hex code (e.g., #4F46E5)")
            }
        }

        request.workingHours?.forEach { validateWorkingHour(it) }
        
        request.branches?.forEach { validateBranch(it) }
    }

    private fun validateWorkingHour(hour: WorkingHourDto) {
        val timeRegex = Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")
        
        if (!hour.isClosed) {
            if (hour.openingTime == null || !hour.openingTime.matches(timeRegex)) {
                throw ValidationException("Invalid opening time for ${hour.dayOfWeek}. Expected format HH:mm")
            }
            if (hour.closingTime == null || !hour.closingTime.matches(timeRegex)) {
                throw ValidationException("Invalid closing time for ${hour.dayOfWeek}. Expected format HH:mm")
            }
        }
    }

    fun validateBranch(branch: SchoolBranchDto) {
        ValidationSchema.branch.name.validate(branch.name)
        ValidationSchema.branch.type.validate(branch.type)
        ValidationSchema.branch.address.validate(branch.address)
        ValidationSchema.branch.city.validate(branch.city)
        ValidationSchema.branch.state.validate(branch.state)
        ValidationSchema.branch.country.validate(branch.country)
        ValidationSchema.branch.postalCode.validate(branch.postalCode)
        ValidationSchema.branch.contactPerson.validate(branch.contactPerson)
        ValidationSchema.branch.phone.validate(branch.phone)
        ValidationSchema.branch.email.validate(branch.email)
    }

    private fun validateEmail(email: String) {
        if (email.isNotBlank() && !email.contains("@")) {
            throw ValidationException("Invalid email format for branch")
        }
    }
}
