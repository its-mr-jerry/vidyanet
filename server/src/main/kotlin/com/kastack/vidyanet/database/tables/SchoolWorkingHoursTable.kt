package com.kastack.vidyanet.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object SchoolWorkingHoursTable : LongIdTable("school_working_hours", "id") {
    val schoolId = reference("school_id", SchoolsTable, onDelete = ReferenceOption.CASCADE)
    val dayOfWeek = varchar("day_of_week", 15) // MONDAY, TUESDAY, etc.
    val openingTime = varchar("opening_time", 10).nullable()
    val closingTime = varchar("closing_time", 10).nullable()
    val isClosed = bool("is_closed").default(false)
}
