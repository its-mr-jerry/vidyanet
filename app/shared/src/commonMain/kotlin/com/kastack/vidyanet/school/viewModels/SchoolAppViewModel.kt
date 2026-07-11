package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.data.repositories.SchoolRepository
import com.kastack.vidyanet.models.schoolUser.SchoolDto
import com.kastack.vidyanet.models.schoolUser.AcademicSettingsDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SchoolAppUiState(
    val school: SchoolDto? = null,
    val academicSettings: AcademicSettingsDto? = null,
    val isLoading: Boolean = false,
    val unreadNotifications: Int = 0
)

class SchoolAppViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SchoolAppUiState())
    val uiState: StateFlow<SchoolAppUiState> = _uiState.asStateFlow()

    fun loadSchoolInfo(schoolId: String) {
        val id = schoolId.toLongOrNull() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val schoolResult = schoolRepository.getSchoolById(id)
            val settingsResult = schoolRepository.getAcademicSettings(id)
            
            _uiState.value = _uiState.value.copy(
                school = schoolResult.getOrNull(),
                academicSettings = settingsResult.getOrNull(),
                isLoading = false,
                unreadNotifications = 0 // Initialize with 0 or fetch from real service
            )
        }
    }
}
