package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.data.repositories.SchoolRepository
import com.kastack.vidyanet.models.schoolUser.SchoolDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SchoolDashboardUiState(
    val school: SchoolDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val studentCount: String = "0",
    val teacherCount: String = "0",
    val staffCount: String = "0",
    val parentCount: String = "0",
    val pendingAdmissions: String = "0",
    val activeClasses: String = "0",
    val studentAttendance: String = "0%",
    val teacherAttendance: String = "0%",
    val todaysFee: String = "$0.00",
    val pendingFee: String = "$0.00"
)

class SchoolDashboardViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SchoolDashboardUiState())
    val uiState: StateFlow<SchoolDashboardUiState> = _uiState.asStateFlow()

    fun loadDashboardData(schoolId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            schoolRepository.getSchoolById(schoolId.toLong()).onSuccess { school ->
                _uiState.value = _uiState.value.copy(
                    school = school,
                    isLoading = false,
                    studentCount = school.studentCount.toString(),
                    teacherCount = school.teacherCount.toString(),
                    staffCount = (school.teacherCount + 12).toString(), // Mocked offset
                    parentCount = (school.studentCount * 0.8).toInt().toString(), // Mocked ratio
                    pendingAdmissions = "24",
                    activeClasses = "42",
                    studentAttendance = "94%",
                    teacherAttendance = "98%",
                    todaysFee = "$12,450.00",
                    pendingFee = "$4,280.00"
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load dashboard data"
                )
            }
        }
    }
}
