package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.core.GlobalStore
import com.kastack.vidyanet.data.DatabaseManager
import com.kastack.vidyanet.data.repositories.SchoolRepository
import com.kastack.vidyanet.data.repositories.UserRepository
import com.kastack.vidyanet.models.schoolUser.SchoolDto
import com.kastack.vidyanet.models.schoolUser.AcademicSettingsDto
import kotlinx.coroutines.async
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
    private val schoolRepository: SchoolRepository,
    private val userRepository: UserRepository,
    private val databaseManager: DatabaseManager,
    private val globalStore: GlobalStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(SchoolAppUiState())
    val uiState: StateFlow<SchoolAppUiState> = _uiState.asStateFlow()

    fun loadSchoolInfo(schoolId: String) {
        val id = schoolId.toLongOrNull() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val schoolDeferred = async { schoolRepository.getSchoolById(id) }
            val settingsDeferred = async { schoolRepository.getAcademicSettings(id) }
            val userDeferred = async { userRepository.getMe() }
            
            val schoolResult = schoolDeferred.await()
            val settingsResult = settingsDeferred.await()
            val userResult = userDeferred.await()

            userResult.onSuccess { user ->
                globalStore.updateCurrentUser(user)
            }
            
            _uiState.value = _uiState.value.copy(
                school = schoolResult.getOrNull(),
                academicSettings = settingsResult.getOrNull(),
                isLoading = false,
                unreadNotifications = 0
            )
        }
    }

    fun logout() {
        databaseManager.clear()
        globalStore.clear()
    }
}
