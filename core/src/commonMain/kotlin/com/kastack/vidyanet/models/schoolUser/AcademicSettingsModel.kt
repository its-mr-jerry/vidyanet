package com.kastack.vidyanet.models.schoolUser

import kotlinx.serialization.Serializable

@Serializable
data class AcademicSessionDto(
    val id: Long? = null,
    val name: String,
    val startDate: String,
    val endDate: String,
    val status: String // CURRENT, UPCOMING, PAST
)

@Serializable
data class HolidayDto(
    val id: Long? = null,
    val name: String,
    val date: String, // YYYY-MM-DD
    val type: String = "HOLIDAY" // HOLIDAY, EVENT, OBSERVANCE
)

@Serializable
data class AcademicSettingsDto(
    val schoolId: Long,
    val gradingScale: String = "LETTER", // LETTER, GPA, PERCENT
    val passMarks: Int = 35,
    val gpaDecimals: Int = 2,
    val isWeightedGpa: Boolean = false,
    val attendanceMode: String = "DAILY", // DAILY, SUBJECT_WISE
    val lateThresholdMinutes: Int = 15,
    val minPromotionPercentage: Int = 40,
    val minPromotionAttendance: Int = 75,
    val requireNoDues: Boolean = true,
    val academicSessions: List<AcademicSessionDto> = emptyList(),
    val holidays: List<HolidayDto> = emptyList()
)


@Serializable
data class UpdateAcademicSettingsRequest(
    val gradingScale: String? = null,
    val passMarks: Int? = null,
    val gpaDecimals: Int? = null,
    val isWeightedGpa: Boolean? = null,
    val attendanceMode: String? = null,
    val lateThresholdMinutes: Int? = null,
    val minPromotionPercentage: Int? = null,
    val minPromotionAttendance: Int? = null,
    val requireNoDues: Boolean? = null
)
