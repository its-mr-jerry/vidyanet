package com.kastack.vidyanet.validators

import com.kastack.vidyanet.models.settings.UpdateNotificationSettingsRequest

object NotificationValidator {
    fun validateUpdate(request: UpdateNotificationSettingsRequest) {
        request.templates?.forEach { template ->
            if (template.content.isBlank()) {
                throw ValidationException("Notification template for ${template.eventId} on ${template.channel} cannot be empty")
            }
            if (template.content.length > 1000) {
                throw ValidationException("Notification template for ${template.eventId} exceeds maximum length of 1000 characters")
            }
        }
    }
}
