package com.kastack.vidyanet.validators

enum class AppInputType {
    TEXT, NUMBER, PHONE, EMAIL
}

data class FieldSchema(
    val label: String,
    val maxLength: Int = Int.MAX_VALUE,
    val minLength: Int = 0,
    val inputType: AppInputType = AppInputType.TEXT,
    val isRequired: Boolean = true
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
                    // For specific cases like Phone (10) or Postal (6), we enforce exact length if maxLength is small
                    if (maxLength < 20 && it.isNotEmpty() && it.length != maxLength) {
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
}

object SchoolSchema {
    val name = FieldSchema("School name", maxLength = 150)
    val phone = FieldSchema("Phone number", maxLength = 10, inputType = AppInputType.PHONE)
    val email = FieldSchema("Email address", maxLength = 150, inputType = AppInputType.EMAIL, isRequired = false)
    val address = FieldSchema("Address", maxLength = 255)
    val city = FieldSchema("City", maxLength = 100)
    val state = FieldSchema("State", maxLength = 100)
    val country = FieldSchema("Country", maxLength = 100)
    val postalCode = FieldSchema("Postal code", maxLength = 6, inputType = AppInputType.NUMBER)
}

object BranchSchema {
    val name = FieldSchema("Branch name", maxLength = 150)
    val type = FieldSchema("Branch type", maxLength = 50)
    val address = FieldSchema("Address", maxLength = 255)
    val city = FieldSchema("City", maxLength = 100)
    val state = FieldSchema("State", maxLength = 100)
    val country = FieldSchema("Country", maxLength = 100)
    val postalCode = FieldSchema("Postal code", maxLength = 6, inputType = AppInputType.NUMBER)
    val contactPerson = FieldSchema("Contact person", maxLength = 100)
    val phone = FieldSchema("Phone number", maxLength = 10, inputType = AppInputType.PHONE)
    val email = FieldSchema("Email", maxLength = 150, inputType = AppInputType.EMAIL, isRequired = false)
}
