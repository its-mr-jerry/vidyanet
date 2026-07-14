package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.core.GlobalStore
import com.kastack.vidyanet.data.repositories.SchoolRepository
import com.kastack.vidyanet.models.settings.*
import com.kastack.vidyanet.permissions.PermissionSchema
import com.kastack.vidyanet.validators.NotificationValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class NotificationSettingsUiState(
    val channels: NotificationChannelSettings = NotificationChannelSettings(),
    val eventRules: List<NotificationEventRule> = emptyList(),
    val templates: List<NotificationTemplateDto> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val canEdit: Boolean = false,
    val error: String? = null,
    val selectedEventId: String = "student_attendance",
    val selectedChannel: NotificationChannel = NotificationChannel.SMS,
    val templateContent: String = ""
)

class NotificationSettingsViewModel(
    private val schoolRepository: SchoolRepository,
    private val globalStore: GlobalStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()
    private var currentSchoolId: String? = null

    fun loadSettings(schoolId: String) {
        currentSchoolId = schoolId
        val id = schoolId.toLongOrNull() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                canEdit = globalStore.hasPermission(PermissionSchema.Settings.MODULE, "EDIT")
            )
            schoolRepository.getNotificationSettings(id)
                .onSuccess { settings ->
                    _uiState.value = _uiState.value.copy(
                        channels = settings.channels,
                        eventRules = settings.eventRules,
                        templates = settings.templates,
                        isLoading = false
                    )
                    updateTemplatePreview()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                }
        }
    }

    private fun updateTemplatePreview() {
        val state = _uiState.value
        val template = state.templates.find { it.eventId == state.selectedEventId && it.channel == state.selectedChannel }
        _uiState.value = state.copy(templateContent = template?.content ?: "No template defined for this event and channel.")
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
        updateTemplatePreview()
    }

    fun updateTemplateContent(content: String) {
        val state = _uiState.value
        val updatedTemplates = state.templates.toMutableList()
        val index = updatedTemplates.indexOfFirst { it.eventId == state.selectedEventId && it.channel == state.selectedChannel }
        
        val newTemplate = NotificationTemplateDto(state.selectedEventId, state.selectedChannel, content)
        if (index != -1) {
            updatedTemplates[index] = newTemplate
        } else {
            updatedTemplates.add(newTemplate)
        }
        
        _uiState.value = state.copy(templateContent = content, templates = updatedTemplates)
    }

    fun saveSettings() {
        val schoolId = currentSchoolId?.toLongOrNull() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            val request = UpdateNotificationSettingsRequest(
                channels = _uiState.value.channels,
                eventRules = _uiState.value.eventRules,
                templates = _uiState.value.templates
            )

            try {
                NotificationValidator.validateUpdate(request)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
                return@launch
            }

            schoolRepository.updateNotificationSettings(schoolId, request)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
                    kotlinx.coroutines.delay(3000.milliseconds)
                    _uiState.value = _uiState.value.copy(saveSuccess = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isSaving = false, error = error.message)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
