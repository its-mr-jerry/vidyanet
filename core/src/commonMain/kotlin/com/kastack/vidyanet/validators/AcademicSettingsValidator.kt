package com.kastack.vidyanet.validators

import com.kastack.vidyanet.models.schoolUser.AcademicSessionDto
import com.kastack.vidyanet.models.schoolUser.HolidayDto
import com.kastack.vidyanet.models.schoolUser.UpdateAcademicSettingsRequest

object AcademicSettingsValidator {

    fun validateUpdate(request: UpdateAcademicSettingsRequest) {
        request.passMarks?.let {
            ValidationSchema.academic.passMarks.validate(it.toString())
            if (it < 0 || it > 100) {
                throw ValidationException("Pass marks must be between 0 and 100")
            }
        }

        request.gpaDecimals?.let {
            ValidationSchema.academic.gpaDecimals.validate(it.toString())
            if (it < 0 || it > 4) {
                throw ValidationException("GPA decimals must be between 0 and 4")
            }
        }

        request.lateThresholdMinutes?.let {
            ValidationSchema.academic.lateThreshold.validate(it.toString())
            if (it < 0 || it > 480) {
                throw ValidationException("Late threshold cannot exceed 480 minutes (8 hours)")
            }
        }

        request.minPromotionPercentage?.let {
            ValidationSchema.academic.minPromotionPercentage.validate(it.toString())
            if (it < 0 || it > 100) {
                throw ValidationException("Minimum promotion percentage must be between 0 and 100")
            }
        }

        request.minPromotionAttendance?.let {
            ValidationSchema.academic.minPromotionAttendance.validate(it.toString())
            if (it < 0 || it > 100) {
                throw ValidationException("Minimum promotion attendance must be between 0 and 100")
            }
        }
    }

    fun validateSession(session: AcademicSessionDto) {
        ValidationSchema.session.name.validate(session.name)
        ValidationSchema.session.startDate.validate(session.startDate)
        ValidationSchema.session.endDate.validate(session.endDate)

        val dateRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        if (!session.startDate.matches(dateRegex)) {
            throw ValidationException("Invalid start date format. Expected YYYY-MM-DD")
        }
        if (!session.endDate.matches(dateRegex)) {
            throw ValidationException("Invalid end date format. Expected YYYY-MM-DD")
        }
    }

    fun validateHoliday(holiday: HolidayDto) {
        if (holiday.name.isBlank()) {
            throw ValidationException("Holiday name cannot be empty")
        }
        val dateRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        if (!holiday.date.matches(dateRegex)) {
            throw ValidationException("Invalid holiday date format. Expected YYYY-MM-DD")
        }
    }
}
