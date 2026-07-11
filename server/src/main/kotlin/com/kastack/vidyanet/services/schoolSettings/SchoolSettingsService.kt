package com.kastack.vidyanet.services.schoolSettings

import com.kastack.vidyanet.database.entities.SchoolBranchEntity
import com.kastack.vidyanet.database.entities.SchoolSettingsEntity
import com.kastack.vidyanet.database.entities.SchoolWorkingHourEntity
import com.kastack.vidyanet.database.entities.toDto
import com.kastack.vidyanet.database.tables.SchoolBranchesTable
import com.kastack.vidyanet.database.tables.SchoolSettingsTable
import com.kastack.vidyanet.database.tables.SchoolWorkingHoursTable
import com.kastack.vidyanet.models.schoolUser.SchoolSettingsDto
import com.kastack.vidyanet.models.schoolUser.UpdateSchoolSettingsRequest
import com.kastack.vidyanet.services.school.SchoolService
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

class SchoolSettingsService(private val schoolService: SchoolService = SchoolService()) {

    fun getSchoolSettings(schoolId: Long): SchoolSettingsDto = transaction {
        val settings =
            SchoolSettingsEntity.find { SchoolSettingsTable.schoolId eq schoolId }.firstOrNull()
                ?: SchoolSettingsEntity.new {
                    this.schoolId = EntityID(schoolId, SchoolSettingsTable)
                }

        val workingHours =
            SchoolWorkingHourEntity.find { SchoolWorkingHoursTable.schoolId eq schoolId }
                .map { it.toDto() }

        val branches = SchoolBranchEntity.find { SchoolBranchesTable.schoolId eq schoolId }
            .map { it.toDto() }

        settings.toDto(workingHours, branches)
    }

    fun updateSchoolSettings(schoolId: Long, request: UpdateSchoolSettingsRequest): SchoolSettingsDto =
        transaction {
            val settings =
                SchoolSettingsEntity.find { SchoolSettingsTable.schoolId eq schoolId }.firstOrNull()
                    ?: SchoolSettingsEntity.new {
                        this.schoolId = EntityID(schoolId, SchoolSettingsTable)
                    }

            request.registrationNumber?.let { settings.registrationNumber = it }
            request.motto?.let { settings.motto = it }
            request.establishmentDate?.let { settings.establishmentDate = it }
            request.affiliationBoard?.let { settings.affiliationBoard = it }
            request.primaryBrandColor?.let { settings.primaryBrandColor = it }
            request.logoBase64?.let { 
                // In a real app, upload this to S3 and store the URL
                // For now, we'll just mock a URL if data was sent
                settings.logoUrl = "https://placehold.co/400x400?text=Logo+Updated"
            }
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

            request.branches?.let { branches ->
                // Simple approach: replace all
                SchoolBranchesTable.deleteWhere { SchoolBranchesTable.schoolId eq schoolId }
                branches.forEach { branch ->
                    SchoolBranchEntity.new {
                        this.schoolId = EntityID(schoolId, SchoolBranchesTable)
                        this.name = branch.name
                        this.type = branch.type
                        this.address = branch.address
                        this.city = branch.city
                        this.state = branch.state
                        this.country = branch.country
                        this.postalCode = branch.postalCode
                        this.contactPerson = branch.contactPerson
                        this.phone = branch.phone
                        this.email = branch.email
                        this.status = branch.status
                    }
                }
            }

            getSchoolSettings(schoolId)
        }
}