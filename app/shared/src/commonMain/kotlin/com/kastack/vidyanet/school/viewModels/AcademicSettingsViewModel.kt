package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.data.repositories.SchoolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AcademicSettingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val academicSessions: List<AcademicSession> = listOf(
        AcademicSession("Session 2024 - 25", "Jun 1, 2024 — May 31, 2025", "Current"),
        AcademicSession("Session 2025 - 26", "Jun 1, 2025 — May 31, 2026", "Upcoming"),
        AcademicSession("Session 2023 - 24", "Jun 1, 2023 — May 31, 2024", "Past")
    ),
    val passMarks: Int = 35,
    val gpaDecimals: Int = 2,
    val lateThreshold: Int = 15,
    val minPromotionAttendance: Int = 75,
    val minPromotionPercentage: Int = 40
)

data class AcademicSession(
    val name: String,
    val duration: String,
    val status: String
)

class AcademicSettingsViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AcademicSettingsUiState())
    val uiState: StateFlow<AcademicSettingsUiState> = _uiState.asStateFlow()

    fun loadSettings(schoolId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            // Simulation
            kotlinx.coroutines.delay(500)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, saveSuccess = false)
            kotlinx.coroutines.delay(1000)
            _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
        }
    }

    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}
