package com.kastack.vidyanet.services.notificationSettings

import com.kastack.vidyanet.database.tables.NotificationEventRulesTable
import com.kastack.vidyanet.database.tables.NotificationSettingsTable
import com.kastack.vidyanet.database.tables.NotificationTemplatesTable
import com.kastack.vidyanet.models.settings.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class NotificationSettingsService {

    fun getSettings(schoolId: Long): NotificationSettingsDto = transaction {
        val settingsRow = NotificationSettingsTable.selectAll().where { NotificationSettingsTable.schoolId eq schoolId }.singleOrNull()
        
        val channels = if (settingsRow == null) {
            NotificationSettingsTable.insert {
                it[NotificationSettingsTable.schoolId] = schoolId
            }
            NotificationChannelSettings()
        } else {
            NotificationChannelSettings(
                emailEnabled = settingsRow[NotificationSettingsTable.emailEnabled],
                smsEnabled = settingsRow[NotificationSettingsTable.smsEnabled],
                pushEnabled = settingsRow[NotificationSettingsTable.pushEnabled]
            )
        }

        val eventRules = NotificationEventRulesTable.selectAll().where { NotificationEventRulesTable.schoolId eq schoolId }
            .map {
                NotificationEventRule(
                    id = it[NotificationEventRulesTable.eventId],
                    name = it[NotificationEventRulesTable.name],
                    description = it[NotificationEventRulesTable.description],
                    category = NotificationCategory.valueOf(it[NotificationEventRulesTable.category]),
                    emailEnabled = it[NotificationEventRulesTable.emailEnabled],
                    smsEnabled = it[NotificationEventRulesTable.smsEnabled],
                    pushEnabled = it[NotificationEventRulesTable.pushEnabled]
                )
            }
            .ifEmpty { 
                seedDefaultRules(schoolId) 
            }

        val templates = NotificationTemplatesTable.selectAll().where { NotificationTemplatesTable.schoolId eq schoolId }
            .map {
                NotificationTemplateDto(
                    eventId = it[NotificationTemplatesTable.eventId],
                    channel = NotificationChannel.valueOf(it[NotificationTemplatesTable.channel]),
                    content = it[NotificationTemplatesTable.content]
                )
            }
            .ifEmpty {
                seedDefaultTemplates(schoolId)
            }

        NotificationSettingsDto(schoolId, channels, eventRules, templates)
    }

    private fun seedDefaultTemplates(schoolId: Long): List<NotificationTemplateDto> {
        val defaults = listOf(
            NotificationTemplateDto("student_attendance", NotificationChannel.SMS, "Dear Parent, your child [Student_Name] is absent today, [Date]. Please contact the school office."),
            NotificationTemplateDto("fee_due", NotificationChannel.SMS, "Reminder: School fee for [Student_Name] is due. Please pay by [Date] to avoid late fine."),
            NotificationTemplateDto("exam_result", NotificationChannel.EMAIL, "The exam results for [Student_Name] have been published. Please login to the portal to view the report card.")
        )
        
        defaults.forEach { template ->
            NotificationTemplatesTable.insert {
                it[NotificationTemplatesTable.schoolId] = schoolId
                it[eventId] = template.eventId
                it[channel] = template.channel.name
                it[content] = template.content
            }
        }
        return defaults
    }

    private fun seedDefaultRules(schoolId: Long): List<NotificationEventRule> {
        val defaults = listOf(
            NotificationEventRule("exam_result", "Exam Result Published", "Notify parents and students when results are released.", NotificationCategory.ACADEMIC, true, false, true),
            NotificationEventRule("student_attendance", "Student Attendance", "Daily absent notification to parents.", NotificationCategory.ACADEMIC, true, true, true),
            NotificationEventRule("fee_due", "Fee Due Reminder", "Recurring reminders for upcoming or late fee payments.", NotificationCategory.FINANCE, true, true, false),
            NotificationEventRule("staff_meeting", "Staff Meeting", "Calendar updates and staff announcement reminders.", NotificationCategory.ADMINISTRATION, true, false, true)
        )
        
        defaults.forEach { rule ->
            NotificationEventRulesTable.insert {
                it[NotificationEventRulesTable.schoolId] = schoolId
                it[eventId] = rule.id
                it[name] = rule.name
                it[description] = rule.description
                it[category] = rule.category.name
                it[emailEnabled] = rule.emailEnabled
                it[smsEnabled] = rule.smsEnabled
                it[pushEnabled] = rule.pushEnabled
            }
        }
        return defaults
    }

    fun updateSettings(schoolId: Long, request: UpdateNotificationSettingsRequest) = transaction {
        request.channels?.let { ch ->
            NotificationSettingsTable.update({ NotificationSettingsTable.schoolId eq schoolId }) {
                it[emailEnabled] = ch.emailEnabled
                it[smsEnabled] = ch.smsEnabled
                it[pushEnabled] = ch.pushEnabled
            }
        }

        request.eventRules?.forEach { rule ->
            NotificationEventRulesTable.update({ (NotificationEventRulesTable.schoolId eq schoolId) and (NotificationEventRulesTable.eventId eq rule.id) }) {
                it[emailEnabled] = rule.emailEnabled
                it[smsEnabled] = rule.smsEnabled
                it[pushEnabled] = rule.pushEnabled
            }
        }

        request.templates?.forEach { template ->
            NotificationTemplatesTable.upsert(NotificationTemplatesTable.schoolId, NotificationTemplatesTable.eventId, NotificationTemplatesTable.channel) {
                it[NotificationTemplatesTable.schoolId] = schoolId
                it[eventId] = template.eventId
                it[channel] = template.channel.name
                it[content] = template.content
            }
        }
    }
}
