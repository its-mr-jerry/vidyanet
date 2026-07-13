@file:Suppress("DEPRECATION")

package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.data.repositories.SchoolRepository
import com.kastack.vidyanet.models.audit.AuditLogDto
import com.kastack.vidyanet.models.audit.AuditStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuditLogsUiState(
    val logs: List<AuditLogDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalEntries: Int = 0,
    val currentPage: Int = 1,
    val pageSize: Int = 10,
    val searchQuery: String = "",
    val selectedModule: String? = null,
    val selectedStatus: AuditStatus? = null
)

class AuditLogsViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuditLogsUiState())
    val uiState: StateFlow<AuditLogsUiState> = _uiState.asStateFlow()
    private var currentSchoolId: String? = null
    private var loadJob: kotlinx.coroutines.Job? = null

    fun init(schoolId: String) {
        if (currentSchoolId == schoolId) return
        currentSchoolId = schoolId
        loadLogs()
    }

    fun loadLogs() {
        val schoolId = currentSchoolId?.toLongOrNull() ?: return
        
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            schoolRepository.getAuditLogs(
                schoolId = schoolId,
                search = _uiState.value.searchQuery.takeIf { it.isNotBlank() },
                module = _uiState.value.selectedModule,
                status = _uiState.value.selectedStatus,
                page = _uiState.value.currentPage,
                pageSize = _uiState.value.pageSize
            ).onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    logs = response.items,
                    totalEntries = response.totalItems.toInt(),
                    isLoading = false
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }
        }
    }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query, currentPage = 1)
        loadLogs()
    }

    fun onModuleFilterChange(module: String?) {
        _uiState.value = _uiState.value.copy(selectedModule = module, currentPage = 1)
        loadLogs()
    }

    fun onStatusFilterChange(status: AuditStatus?) {
        _uiState.value = _uiState.value.copy(selectedStatus = status, currentPage = 1)
        loadLogs()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            selectedModule = null,
            selectedStatus = null,
            currentPage = 1
        )
        loadLogs()
    }

    fun onPageChange(page: Int) {
        _uiState.value = _uiState.value.copy(currentPage = page)
        loadLogs()
    }

    fun onPageSizeChange(size: Int) {
        _uiState.value = _uiState.value.copy(pageSize = size, currentPage = 1)
        loadLogs()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
