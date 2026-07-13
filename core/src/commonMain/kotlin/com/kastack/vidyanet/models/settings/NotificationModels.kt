package com.kastack.vidyanet.models.settings

import kotlinx.serialization.Serializable

@Serializable
enum class NotificationChannel {
    EMAIL, SMS, PUSH
}

@Serializable
enum class NotificationCategory {
    ACADEMIC, FINANCE, ADMINISTRATION
}

@Serializable
data class NotificationChannelSettings(
    val emailEnabled: Boolean = true,
    val smsEnabled: Boolean = false,
    val pushEnabled: Boolean = true
)

@Serializable
data class NotificationEventRule(
    val id: String,
    val name: String,
    val description: String,
    val category: NotificationCategory,
    val emailEnabled: Boolean,
    val smsEnabled: Boolean,
    val pushEnabled: Boolean
)

@Serializable
data class NotificationTemplateDto(
    val eventId: String,
    val channel: NotificationChannel,
    val content: String
)

@Serializable
data class NotificationSettingsDto(
    val schoolId: Long,
    val channels: NotificationChannelSettings,
    val eventRules: List<NotificationEventRule>,
    val templates: List<NotificationTemplateDto>
)

@Serializable
data class UpdateNotificationSettingsRequest(
    val channels: NotificationChannelSettings? = null,
    val eventRules: List<NotificationEventRule>? = null,
    val templates: List<NotificationTemplateDto>? = null
)
