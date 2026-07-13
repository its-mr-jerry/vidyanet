package com.kastack.vidyanet.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object NotificationSettingsTable : LongIdTable("notification_settings", "id") {
    val schoolId = reference("school_id", SchoolsTable, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val emailEnabled = bool("email_enabled").default(true)
    val smsEnabled = bool("sms_enabled").default(false)
    val pushEnabled = bool("push_enabled").default(true)
}

object NotificationEventRulesTable : LongIdTable("notification_event_rules", "id") {
    val schoolId = reference("school_id", SchoolsTable, onDelete = ReferenceOption.CASCADE)
    val eventId = varchar("event_id", 50)
    val name = varchar("name", 100)
    val description = varchar("description", 255)
    val category = varchar("category", 30) // ACADEMIC, FINANCE, ADMINISTRATION
    val emailEnabled = bool("email_enabled").default(true)
    val smsEnabled = bool("sms_enabled").default(false)
    val pushEnabled = bool("push_enabled").default(true)

    init {
        uniqueIndex(schoolId, eventId)
    }
}

object NotificationTemplatesTable : LongIdTable("notification_templates", "id") {
    val schoolId = reference("school_id", SchoolsTable, onDelete = ReferenceOption.CASCADE)
    val eventId = varchar("event_id", 50)
    val channel = varchar("channel", 10) // EMAIL, SMS, PUSH
    val content = text("content")

    init {
        uniqueIndex(schoolId, eventId, channel)
    }
}
