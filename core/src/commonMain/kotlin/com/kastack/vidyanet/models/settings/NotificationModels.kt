package com.kastack.vidyanet.models.settings

import kotlinx.serialization.Serializable

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
enum class NotificationCategory {
    ACADEMIC, FINANCE, ADMINISTRATION
}

@Serializable
data class NotificationTemplateDto(
    val eventId: String,
    val channel: NotificationChannel,
    val content: String
)

@Serializable
enum class NotificationChannel {
    EMAIL, SMS, PUSH
}
