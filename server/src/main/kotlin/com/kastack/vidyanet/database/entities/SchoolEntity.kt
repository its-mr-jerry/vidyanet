package com.kastack.vidyanet.database.entities

import com.kastack.vidyanet.database.tables.SchoolsTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class SchoolEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SchoolEntity>(SchoolsTable)

    var schoolCode by SchoolsTable.schoolCode
    var schoolName by SchoolsTable.schoolName
    var schoolType by SchoolsTable.schoolType
    var phone by SchoolsTable.phone
    var email by SchoolsTable.email
    var website by SchoolsTable.website
    var address by SchoolsTable.address
    var city by SchoolsTable.city
    var state by SchoolsTable.state
    var country by SchoolsTable.country
    var postalCode by SchoolsTable.postalCode
    var logoUrl by SchoolsTable.logoUrl
    var status by SchoolsTable.status
    var createdAt by SchoolsTable.createdAt
    var updatedAt by SchoolsTable.updatedAt
}
