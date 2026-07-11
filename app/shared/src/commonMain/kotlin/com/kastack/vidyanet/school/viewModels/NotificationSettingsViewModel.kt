package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.models.settings.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class NotificationSettingsUiState(
    val channels: NotificationChannelSettings = NotificationChannelSettings(),
    val eventRules: List<NotificationEventRule> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val selectedEventId: String = "student_attendance",
    val selectedChannel: NotificationChannel = NotificationChannel.SMS,
    val templateContent: String = "Dear Parent, your child [Student_Name] is absent today, [Date]. Please contact the school office."
)

class NotificationSettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(500.milliseconds)
            
            val mockRules = listOf(
                NotificationEventRule("exam_result", "Exam Result Published", "Notify parents and students when results are released.", NotificationCategory.ACADEMIC, true, false, true),
                NotificationEventRule("student_attendance", "Student Attendance", "Daily absent notification to parents.", NotificationCategory.ACADEMIC, true, true, true),
                NotificationEventRule("fee_due", "Fee Due Reminder", "Recurring reminders for upcoming or late fee payments.", NotificationCategory.FINANCE, true, true, false),
                NotificationEventRule("staff_meeting", "Staff Meeting", "Calendar updates and staff announcement reminders.", NotificationCategory.ADMINISTRATION, true, false, true)
            )
            
            _uiState.value = _uiState.value.copy(
                eventRules = mockRules,
                isLoading = false
            )
        }
    }

    fun updateChannelEnabled(channel: NotificationChannel, enabled: Boolean) {
        val currentChannels = _uiState.value.channels
        val newChannels = when (channel) {
            NotificationChannel.EMAIL -> currentChannels.copy(emailEnabled = enabled)
            NotificationChannel.SMS -> currentChannels.copy(smsEnabled = enabled)
            NotificationChannel.PUSH -> currentChannels.copy(pushEnabled = enabled)
        }
        _uiState.value = _uiState.value.copy(channels = newChannels)
    }

    fun toggleEventPermission(eventId: String, channel: NotificationChannel, enabled: Boolean) {
        val updatedRules = _uiState.value.eventRules.map {
            if (it.id == eventId) {
                when (channel) {
                    NotificationChannel.EMAIL -> it.copy(emailEnabled = enabled)
                    NotificationChannel.SMS -> it.copy(smsEnabled = enabled)
                    NotificationChannel.PUSH -> it.copy(pushEnabled = enabled)
                }
            } else it
        }
        _uiState.value = _uiState.value.copy(eventRules = updatedRules)
    }

    fun selectTemplate(eventId: String, channel: NotificationChannel) {
        _uiState.value = _uiState.value.copy(selectedEventId = eventId, selectedChannel = channel)
        // In real app, load template content from repository
    }

    fun updateTemplateContent(content: String) {
        _uiState.value = _uiState.value.copy(templateContent = content)
    }

    fun saveSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            delay(1000.milliseconds)
            _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
            delay(3000.milliseconds)
            _uiState.value = _uiState.value.copy(saveSuccess = false)
        }
    }
}
