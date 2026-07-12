package com.kastack.vidyanet.services.academicSettings

import com.kastack.vidyanet.database.tables.AcademicSessionsTable
import com.kastack.vidyanet.database.tables.AcademicSettingsTable
import com.kastack.vidyanet.database.tables.HolidaysTable
import com.kastack.vidyanet.models.schoolUser.AcademicSessionDto
import com.kastack.vidyanet.models.schoolUser.AcademicSettingsDto
import com.kastack.vidyanet.models.schoolUser.HolidayDto
import com.kastack.vidyanet.models.schoolUser.UpdateAcademicSettingsRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class AcademicSettingsService {

    fun getAcademicSettings(schoolId: Long): AcademicSettingsDto {
        return transaction {
            val settings = AcademicSettingsTable.selectAll().where { AcademicSettingsTable.schoolId eq schoolId }.singleOrNull()
            val sessions = AcademicSessionsTable.selectAll().where { AcademicSessionsTable.schoolId eq schoolId }
                .map {
                    AcademicSessionDto(
                        id = it[AcademicSessionsTable.id].value,
                        name = it[AcademicSessionsTable.name],
                        startDate = it[AcademicSessionsTable.startDate],
                        endDate = it[AcademicSessionsTable.endDate],
                        status = it[AcademicSessionsTable.status]
                    )
                }
            
            val holidays = HolidaysTable.selectAll().where { HolidaysTable.schoolId eq schoolId }
                .map {
                    HolidayDto(
                        id = it[HolidaysTable.id].value,
                        name = it[HolidaysTable.name],
                        date = it[HolidaysTable.date],
                        type = it[HolidaysTable.type]
                    )
                }

            if (settings == null) {
                // Initialize default settings if not exists
                AcademicSettingsTable.insert {
                    it[AcademicSettingsTable.schoolId] = schoolId
                }
                AcademicSettingsDto(schoolId = schoolId, academicSessions = sessions, holidays = holidays)
            } else {
                AcademicSettingsDto(
                    schoolId = schoolId,
                    gradingScale = settings[AcademicSettingsTable.gradingScale],
                    passMarks = settings[AcademicSettingsTable.passMarks],
                    gpaDecimals = settings[AcademicSettingsTable.gpaDecimals],
                    isWeightedGpa = settings[AcademicSettingsTable.isWeightedGpa],
                    attendanceMode = settings[AcademicSettingsTable.attendanceMode],
                    lateThresholdMinutes = settings[AcademicSettingsTable.lateThresholdMinutes],
                    minPromotionPercentage = settings[AcademicSettingsTable.minPromotionPercentage],
                    minPromotionAttendance = settings[AcademicSettingsTable.minPromotionAttendance],
                    requireNoDues = settings[AcademicSettingsTable.requireNoDues],
                    academicSessions = sessions,
                    holidays = holidays
                )
            }
        }
    }

    fun updateAcademicSettings(schoolId: Long, request: UpdateAcademicSettingsRequest) {
        transaction {
            AcademicSettingsTable.update({ AcademicSettingsTable.schoolId eq schoolId }) {
                request.gradingScale?.let { v -> it[gradingScale] = v }
                request.passMarks?.let { v -> it[passMarks] = v }
                request.gpaDecimals?.let { v -> it[gpaDecimals] = v }
                request.isWeightedGpa?.let { v -> it[isWeightedGpa] = v }
                request.attendanceMode?.let { v -> it[attendanceMode] = v }
                request.lateThresholdMinutes?.let { v -> it[lateThresholdMinutes] = v }
                request.minPromotionPercentage?.let { v -> it[minPromotionPercentage] = v }
                request.minPromotionAttendance?.let { v -> it[minPromotionAttendance] = v }
                request.requireNoDues?.let { v -> it[requireNoDues] = v }
            }
        }
    }

    fun addAcademicSession(schoolId: Long, session: AcademicSessionDto) {
        transaction {
            AcademicSessionsTable.insert {
                it[AcademicSessionsTable.schoolId] = schoolId
                it[name] = session.name
                it[startDate] = session.startDate
                it[endDate] = session.endDate
                it[status] = session.status
            }
        }
    }

    fun deleteAcademicSession(schoolId: Long, sessionId: Long) {
        transaction {
            AcademicSessionsTable.deleteWhere { 
                (AcademicSessionsTable.id eq sessionId) and (AcademicSessionsTable.schoolId eq schoolId)
            }
        }
    }

    fun addHoliday(schoolId: Long, holiday: HolidayDto) {
        transaction {
            HolidaysTable.insert {
                it[HolidaysTable.schoolId] = schoolId
                it[name] = holiday.name
                it[date] = holiday.date
                it[type] = holiday.type
            }
        }
    }

    fun deleteHoliday(schoolId: Long, holidayId: Long) {
        transaction {
            HolidaysTable.deleteWhere { 
                (HolidaysTable.id eq holidayId) and (HolidaysTable.schoolId eq schoolId)
            }
        }
    }
}
