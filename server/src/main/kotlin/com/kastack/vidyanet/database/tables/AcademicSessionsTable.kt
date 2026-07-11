package com.kastack.vidyanet.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object AcademicSessionsTable : LongIdTable("academic_sessions", "id") {
    val schoolId = reference("school_id", SchoolsTable, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 100)
    val startDate = varchar("start_date", 20)
    val endDate = varchar("end_date", 20)
    val status = varchar("status", 20).default("UPCOMING") // CURRENT, UPCOMING, PAST
}
