package com.kastack.vidyanet.validators

enum class AppInputType {
    TEXT, NUMBER, PHONE, EMAIL
}

data class FieldSchema(
    val label: String,
    val maxLength: Int = Int.MAX_VALUE,
    val minLength: Int = 0,
    val inputType: AppInputType = AppInputType.TEXT,
    val isRequired: Boolean = true,
    val isExactLength: Boolean = false
) {
    /**
     * Checks if the input is valid during real-time typing.
     */
    fun isValidRealTime(input: String): Boolean {
        if (input.length > maxLength) return false
        return when (inputType) {
            AppInputType.NUMBER, AppInputType.PHONE -> input.all { it.isDigit() }
            else -> true
        }
    }

    /**
     * Final validation for submission.
     */
    fun validate(value: String?) {
        if (isRequired && value.isNullOrBlank()) {
            throw ValidationException("$label is required")
        }
        
        value?.let {
            if (it.length > maxLength) {
                throw ValidationException("$label cannot exceed $maxLength characters")
            }
            if (it.length < minLength) {
                throw ValidationException("$label must be at least $minLength characters")
            }
            
            when (inputType) {
                AppInputType.NUMBER, AppInputType.PHONE -> {
                    if (it.isNotEmpty() && !it.all { char -> char.isDigit() }) {
                        throw ValidationException("$label must contain only digits")
                    }
                    
                    if (isExactLength && it.isNotEmpty() && it.length != maxLength) {
                         throw ValidationException("$label must be exactly $maxLength digits")
                    }
                }
                AppInputType.EMAIL -> {
                    if (it.isNotEmpty() && !it.contains("@")) {
                        throw ValidationException("Invalid $label format")
                    }
                }
                else -> {}
            }
        }
    }
}

object ValidationSchema {
    val school = SchoolSchema
    val branch = BranchSchema
    val academic = AcademicSchema
    val session = AcademicSessionSchema
    val user = UserSchema
    val role = RoleSchema
}

object SchoolSchema {
    val name = FieldSchema("School name", maxLength = 150)
    val regNo = FieldSchema("Registration number", maxLength = 50, isRequired = false)
    val motto = FieldSchema("School motto", maxLength = 255, isRequired = false)
    val affiliationBoard = FieldSchema("Affiliation board", maxLength = 100, isRequired = false)
    val establishmentDate = FieldSchema("Establishment date", maxLength = 20, isRequired = false)
    val phone = FieldSchema("Phone number", maxLength = 10, inputType = AppInputType.PHONE, isExactLength = true)
    val email = FieldSchema("Email address", maxLength = 150, inputType = AppInputType.EMAIL, isRequired = false)
    val website = FieldSchema("Website", maxLength = 255, isRequired = false)
    val address = FieldSchema("Address", maxLength = 255)
    val city = FieldSchema("City", maxLength = 100)
    val state = FieldSchema("State", maxLength = 100)
    val country = FieldSchema("Country", maxLength = 100)
    val postalCode = FieldSchema("Postal code", maxLength = 6, inputType = AppInputType.NUMBER, isExactLength = true)
}

object BranchSchema {
    val name = FieldSchema("Branch name", maxLength = 150)
    val type = FieldSchema("Branch type", maxLength = 50)
    val address = FieldSchema("Address", maxLength = 255)
    val city = FieldSchema("City", maxLength = 100)
    val state = FieldSchema("State", maxLength = 100)
    val country = FieldSchema("Country", maxLength = 100)
    val postalCode = FieldSchema("Postal code", maxLength = 6, inputType = AppInputType.NUMBER, isExactLength = true)
    val contactPerson = FieldSchema("Contact person", maxLength = 100)
    val phone = FieldSchema("Phone number", maxLength = 10, inputType = AppInputType.PHONE, isExactLength = true)
    val email = FieldSchema("Email", maxLength = 150, inputType = AppInputType.EMAIL, isRequired = false)
}

object AcademicSchema {
    val passMarks = FieldSchema("Pass marks", maxLength = 3, inputType = AppInputType.NUMBER)
    val gpaDecimals = FieldSchema("GPA decimals", maxLength = 1, inputType = AppInputType.NUMBER)
    val lateThreshold = FieldSchema("Late threshold", maxLength = 3, inputType = AppInputType.NUMBER)
    val minPromotionPercentage = FieldSchema("Min promotion percentage", maxLength = 3, inputType = AppInputType.NUMBER)
    val minPromotionAttendance = FieldSchema("Min promotion attendance", maxLength = 3, inputType = AppInputType.NUMBER)
}

object AcademicSessionSchema {
    val name = FieldSchema("Session name", maxLength = 100)
    val startDate = FieldSchema("Start date", maxLength = 10)
    val endDate = FieldSchema("End date", maxLength = 10)
}

object UserSchema {
    val fullName = FieldSchema("Full name", maxLength = 100)
    val phone = FieldSchema("Phone number", maxLength = 10, inputType = AppInputType.PHONE, isExactLength = true)
    val email = FieldSchema("Email address", maxLength = 150, inputType = AppInputType.EMAIL, isRequired = false)
}

object RoleSchema {
    val roleCode = FieldSchema("Role code", maxLength = 50)
    val roleName = FieldSchema("Role name", maxLength = 100)
    val description = FieldSchema("Description", maxLength = 255, isRequired = false)
}
