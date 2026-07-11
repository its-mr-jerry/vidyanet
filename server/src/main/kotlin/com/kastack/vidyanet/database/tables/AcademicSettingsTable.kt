package com.kastack.vidyanet.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object AcademicSettingsTable : LongIdTable("academic_settings", "id") {
    val schoolId = reference("school_id", SchoolsTable, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val gradingScale = varchar("grading_scale", 20).default("LETTER")
    val passMarks = integer("pass_marks").default(35)
    val gpaDecimals = integer("gpa_decimals").default(2)
    val isWeightedGpa = bool("is_weighted_gpa").default(false)
    val attendanceMode = varchar("attendance_mode", 20).default("DAILY")
    val lateThresholdMinutes = integer("late_threshold_minutes").default(15)
    val minPromotionPercentage = integer("min_promotion_percentage").default(40)
    val minPromotionAttendance = integer("min_promotion_attendance").default(75)
    val requireNoDues = bool("require_no_dues").default(true)
}
