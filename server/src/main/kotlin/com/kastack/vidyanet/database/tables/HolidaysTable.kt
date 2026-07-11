package com.kastack.vidyanet.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object HolidaysTable : LongIdTable("holidays", "id") {
    val schoolId = reference("school_id", SchoolsTable, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 100)
    val date = varchar("date", 20)
    val type = varchar("type", 20).default("HOLIDAY")
}
