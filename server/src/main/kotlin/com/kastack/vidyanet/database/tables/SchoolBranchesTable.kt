package com.kastack.vidyanet.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object SchoolBranchesTable : LongIdTable("school_branches", "id") {
    val schoolId = reference("school_id", SchoolsTable, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 150)
    val type = varchar("type", 50)
    val address = varchar("address", 255)
    val city = varchar("city", 100)
    val state = varchar("state", 100)
    val country = varchar("country", 100)
    val postalCode = varchar("postal_code", 20)
    val contactPerson = varchar("contact_person", 100)
    val phone = varchar("phone", 20)
    val email = varchar("email", 150).nullable()
    val status = varchar("status", 20).default("ACTIVE")
}
