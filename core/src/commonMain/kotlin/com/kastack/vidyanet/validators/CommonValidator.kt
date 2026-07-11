package com.kastack.vidyanet.validators

object CommonValidator {

    /**
     * Reusable logic for real-time input filtering in Compose fields.
     * Prevents non-numeric input and enforces a maximum length.
     */
    fun isValidNumericInput(input: String, maxLength: Int): Boolean {
        return input.length <= maxLength && input.all { it.isDigit() }
    }

    /**
     * Final validation for submission.
     */
    fun validateExactDigits(input: String, length: Int, fieldName: String) {
        if (input.length != length || !input.all { it.isDigit() }) {
            throw ValidationException("$fieldName must be exactly $length digits")
        }
    }
}
