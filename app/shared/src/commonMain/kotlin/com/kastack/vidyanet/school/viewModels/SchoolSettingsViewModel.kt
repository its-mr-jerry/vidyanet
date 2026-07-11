package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.commonUi.components.AppDialogState
import com.kastack.vidyanet.commonUi.components.AppDialogType
import com.kastack.vidyanet.data.repositories.SchoolRepository
import com.kastack.vidyanet.models.schoolUser.SchoolDto
import com.kastack.vidyanet.models.schoolUser.SchoolSettingsDto
import com.kastack.vidyanet.models.schoolUser.UpdateSchoolSettingsRequest
import com.kastack.vidyanet.validators.SchoolSettingsValidator
import com.kastack.vidyanet.validators.ValidationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SchoolSettingsUiState(
    val school: SchoolDto? = null,
    val settings: SchoolSettingsDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val dialogState: AppDialogState = AppDialogState()
)

class SchoolSettingsViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SchoolSettingsUiState())
    val uiState: StateFlow<SchoolSettingsUiState> = _uiState.asStateFlow()

    fun loadSettings(schoolId: String) {
        val id = schoolId.toLongOrNull() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val schoolResult = schoolRepository.getSchoolById(id)
            val settingsResult = schoolRepository.getSchoolSettings(id)

            if (schoolResult.isSuccess && settingsResult.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    school = schoolResult.getOrNull(),
                    settings = settingsResult.getOrNull(),
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = schoolResult.exceptionOrNull()?.message ?: settingsResult.exceptionOrNull()?.message ?: "Failed to load data"
                )
            }
        }
    }

    fun saveSettings(schoolId: String, request: UpdateSchoolSettingsRequest) {
        val id = schoolId.toLongOrNull() ?: return
        
        try {
            SchoolSettingsValidator.validateUpdate(request)
        } catch (e: ValidationException) {
            _uiState.value = _uiState.value.copy(
                dialogState = AppDialogState(
                    isVisible = true,
                    type = AppDialogType.ERROR,
                    title = "Validation Error",
                    message = e.message ?: "Invalid data provided.",
                    onConfirm = { hideDialog() }
                )
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true, 
                saveSuccess = false,
                dialogState = AppDialogState(isVisible = true, type = AppDialogType.LOADING, title = "Saving Settings", message = "Please wait while we update your school configurations...")
            )
            
            schoolRepository.updateSchoolSettings(id, request).onSuccess { updatedSettings ->
                _uiState.value = _uiState.value.copy(
                    settings = updatedSettings,
                    isSaving = false,
                    saveSuccess = true,
                    dialogState = AppDialogState(
                        isVisible = true,
                        type = AppDialogType.SUCCESS,
                        title = "Success",
                        message = "Institutional settings have been updated successfully.",
                        onConfirm = { hideDialog() }
                    )
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    dialogState = AppDialogState(
                        isVisible = true,
                        type = AppDialogType.ERROR,
                        title = "Update Failed",
                        message = e.message ?: "An unexpected error occurred while saving settings.",
                        onConfirm = { hideDialog() }
                    )
                )
            }
        }
    }

    fun hideDialog() {
        _uiState.value = _uiState.value.copy(dialogState = _uiState.value.dialogState.copy(isVisible = false))
    }
    
    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}
