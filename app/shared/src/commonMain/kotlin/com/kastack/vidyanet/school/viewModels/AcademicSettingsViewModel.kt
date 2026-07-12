package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.data.repositories.SchoolRepository
import com.kastack.vidyanet.models.schoolUser.AcademicSessionDto
import com.kastack.vidyanet.models.schoolUser.AcademicSettingsDto
import com.kastack.vidyanet.models.schoolUser.HolidayDto
import com.kastack.vidyanet.models.schoolUser.UpdateAcademicSettingsRequest
import com.kastack.vidyanet.validators.AcademicSettingsValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AcademicSettingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val settings: AcademicSettingsDto? = null
)

class AcademicSettingsViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AcademicSettingsUiState())
    val uiState: StateFlow<AcademicSettingsUiState> = _uiState.asStateFlow()

    fun loadSettings(schoolId: String) {
        val id = schoolId.toLongOrNull() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            schoolRepository.getAcademicSettings(id)
                .onSuccess { settings ->
                    _uiState.value = _uiState.value.copy(isLoading = false, settings = settings)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                }
        }
    }

    fun saveSettings(schoolId: String, request: UpdateAcademicSettingsRequest) {
        val id = schoolId.toLongOrNull() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, saveSuccess = false, error = null)
            
            try {
                AcademicSettingsValidator.validateUpdate(request)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
                return@launch
            }

            schoolRepository.updateAcademicSettings(id, request)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
                    loadSettings(schoolId)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isSaving = false, error = error.message)
                }
        }
    }

    fun addSession(schoolId: String, name: String, startDate: String, endDate: String) {
        val id = schoolId.toLongOrNull() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            val session = AcademicSessionDto(name = name, startDate = startDate, endDate = endDate, status = "UPCOMING")
            
            try {
                AcademicSettingsValidator.validateSession(session)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
                return@launch
            }

            schoolRepository.addAcademicSession(id, session)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isSaving = false)
                    loadSettings(schoolId)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isSaving = false, error = error.message)
                }
        }
    }

    fun deleteSession(schoolId: String, sessionId: Long) {
        viewModelScope.launch {
            schoolRepository.deleteAcademicSession(sessionId)
                .onSuccess {
                    loadSettings(schoolId)
                }
        }
    }

    fun addHoliday(schoolId: String, name: String, date: String) {
        val id = schoolId.toLongOrNull() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val holiday = HolidayDto(name = name, date = date)
            schoolRepository.addHoliday(id, holiday)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isSaving = false)
                    loadSettings(schoolId)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isSaving = false, error = error.message)
                }
        }
    }

    fun deleteHoliday(schoolId: String, holidayId: Long) {
        viewModelScope.launch {
            schoolRepository.deleteHoliday(holidayId)
                .onSuccess {
                    loadSettings(schoolId)
                }
        }
    }

    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
