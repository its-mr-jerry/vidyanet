package com.kastack.vidyanet.database.entities

import com.kastack.vidyanet.database.tables.SchoolSettingsTable
import com.kastack.vidyanet.database.tables.SchoolWorkingHoursTable
import com.kastack.vidyanet.database.tables.SchoolBranchesTable
import com.kastack.vidyanet.models.schoolUser.SchoolBranchDto
import com.kastack.vidyanet.models.schoolUser.SchoolSettingsDto
import com.kastack.vidyanet.models.schoolUser.WorkingHourDto
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class SchoolSettingsEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SchoolSettingsEntity>(SchoolSettingsTable)

    var schoolId by SchoolSettingsTable.schoolId
    var registrationNumber by SchoolSettingsTable.registrationNumber
    var motto by SchoolSettingsTable.motto
    var establishmentDate by SchoolSettingsTable.establishmentDate
    var affiliationBoard by SchoolSettingsTable.affiliationBoard
    var primaryBrandColor by SchoolSettingsTable.primaryBrandColor
    var logoUrl by SchoolSettingsTable.logoUrl
    var isMaintenanceMode by SchoolSettingsTable.isMaintenanceMode
}

class SchoolWorkingHourEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SchoolWorkingHourEntity>(SchoolWorkingHoursTable)

    var schoolId by SchoolWorkingHoursTable.schoolId
    var dayOfWeek by SchoolWorkingHoursTable.dayOfWeek
    var openingTime by SchoolWorkingHoursTable.openingTime
    var closingTime by SchoolWorkingHoursTable.closingTime
    var isClosed by SchoolWorkingHoursTable.isClosed
}

class SchoolBranchEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SchoolBranchEntity>(SchoolBranchesTable)

    var schoolId by SchoolBranchesTable.schoolId
    var name by SchoolBranchesTable.name
    var type by SchoolBranchesTable.type
    var address by SchoolBranchesTable.address
    var city by SchoolBranchesTable.city
    var state by SchoolBranchesTable.state
    var country by SchoolBranchesTable.country
    var postalCode by SchoolBranchesTable.postalCode
    var contactPerson by SchoolBranchesTable.contactPerson
    var phone by SchoolBranchesTable.phone
    var email by SchoolBranchesTable.email
    var status by SchoolBranchesTable.status
}

fun SchoolSettingsEntity.toDto(workingHours: List<WorkingHourDto>, branches: List<SchoolBranchDto>) = SchoolSettingsDto(
    schoolId = schoolId.value,
    registrationNumber = registrationNumber,
    motto = motto,
    establishmentDate = establishmentDate,
    affiliationBoard = affiliationBoard,
    primaryBrandColor = primaryBrandColor,
    logoUrl = logoUrl,
    isMaintenanceMode = isMaintenanceMode,
    workingHours = workingHours,
    branches = branches
)

fun SchoolWorkingHourEntity.toDto() = WorkingHourDto(
    id = id.value,
    dayOfWeek = dayOfWeek,
    openingTime = openingTime,
    closingTime = closingTime,
    isClosed = isClosed
)

fun SchoolBranchEntity.toDto() = SchoolBranchDto(
    id = id.value,
    name = name,
    type = type,
    address = address,
    city = city,
    state = state,
    country = country,
    postalCode = postalCode,
    contactPerson = contactPerson,
    phone = phone,
    email = email,
    status = status
)
