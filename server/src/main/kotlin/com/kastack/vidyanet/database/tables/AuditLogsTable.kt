package com.kastack.vidyanet.database.tables

import com.kastack.vidyanet.models.audit.AuditStatus
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object AuditLogsTable : LongIdTable("audit_logs", "id") {
    val schoolId = reference("school_id", SchoolsTable, onDelete = ReferenceOption.CASCADE).nullable()
    val timestamp = timestamp("timestamp")
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.SET_NULL).nullable()
    val action = varchar("action", 100)
    val actionDetails = text("action_details").nullable()
    val module = varchar("module", 50)
    val status = enumerationByName("status", 20, AuditStatus::class)
    val ipAddress = varchar("ip_address", 45).nullable()
    val userAgent = text("user_agent").nullable()
}
