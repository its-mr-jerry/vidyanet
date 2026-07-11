package com.kastack.vidyanet.services.school

import com.kastack.vidyanet.database.entities.*
import com.kastack.vidyanet.database.tables.SchoolSettingsTable
import com.kastack.vidyanet.database.tables.SchoolWorkingHoursTable
import com.kastack.vidyanet.database.tables.SchoolBranchesTable
import com.kastack.vidyanet.models.schoolUser.SchoolSettingsDto
import com.kastack.vidyanet.models.schoolUser.UpdateSchoolSettingsRequest
import com.kastack.vidyanet.models.schoolUser.UpdateSchoolRequest
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

class SchoolSettingsService(private val schoolService: SchoolService = SchoolService()) {

    fun getSchoolSettings(schoolId: Long): SchoolSettingsDto = transaction {
        val settings = SchoolSettingsEntity.find { SchoolSettingsTable.schoolId eq schoolId }.firstOrNull()
            ?: SchoolSettingsEntity.new {
                this.schoolId = EntityID(schoolId, SchoolSettingsTable)
            }

        val workingHours = SchoolWorkingHourEntity.find { SchoolWorkingHoursTable.schoolId eq schoolId }
            .map { it.toDto() }

        val branches = SchoolBranchEntity.find { SchoolBranchesTable.schoolId eq schoolId }
            .map { it.toDto() }

        settings.toDto(workingHours, branches)
    }

    fun updateSchoolSettings(schoolId: Long, request: UpdateSchoolSettingsRequest): SchoolSettingsDto = transaction {
        val settings = SchoolSettingsEntity.find { SchoolSettingsTable.schoolId eq schoolId }.firstOrNull()
            ?: SchoolSettingsEntity.new {
                this.schoolId = EntityID(schoolId, SchoolSettingsTable)
            }

        request.registrationNumber?.let { settings.registrationNumber = it }
        request.motto?.let { settings.motto = it }
        request.establishmentDate?.let { settings.establishmentDate = it }
        request.affiliationBoard?.let { settings.affiliationBoard = it }
        request.primaryBrandColor?.let { settings.primaryBrandColor = it }
        request.isMaintenanceMode?.let { settings.isMaintenanceMode = it }

        request.workingHours?.let { hours ->
            // Simple approach: replace all
            SchoolWorkingHoursTable.deleteWhere { SchoolWorkingHoursTable.schoolId eq schoolId }
            hours.forEach { hour ->
                SchoolWorkingHourEntity.new {
                    this.schoolId = EntityID(schoolId, SchoolWorkingHoursTable)
                    this.dayOfWeek = hour.dayOfWeek
                    this.openingTime = hour.openingTime
                    this.closingTime = hour.closingTime
                    this.isClosed = hour.isClosed
                }
            }
        }

        getSchoolSettings(schoolId)
    }
}
