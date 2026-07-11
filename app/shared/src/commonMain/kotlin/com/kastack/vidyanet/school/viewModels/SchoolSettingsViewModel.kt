package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.data.repositories.SchoolRepository
import com.kastack.vidyanet.models.schoolUser.SchoolDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SchoolSettingsUiState(
    val school: SchoolDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

class SchoolSettingsViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SchoolSettingsUiState())
    val uiState: StateFlow<SchoolSettingsUiState> = _uiState.asStateFlow()

    fun loadSettings(schoolId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            schoolRepository.getSchoolById(schoolId.toLong()).onSuccess { school ->
                _uiState.value = _uiState.value.copy(
                    school = school,
                    isLoading = false
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load school settings"
                )
            }
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, saveSuccess = false)
            // In a real app, we would collect all field values and send update request
            // For now, we simulate success
            kotlinx.coroutines.delay(1000)
            _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
        }
    }
    
    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}
