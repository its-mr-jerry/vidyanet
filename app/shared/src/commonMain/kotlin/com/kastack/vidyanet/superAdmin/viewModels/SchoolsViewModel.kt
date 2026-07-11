package com.kastack.vidyanet.superAdmin.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.data.repositories.SchoolRepository
import com.kastack.vidyanet.models.schoolUser.SchoolDto
import com.kastack.vidyanet.models.schoolUser.CreateSchoolRequest
import com.kastack.vidyanet.models.schoolUser.SchoolStatus
import com.kastack.vidyanet.models.schoolUser.UpdateSchoolRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SchoolDetail(
    val id: String,
    val name: String,
    val code: String,
    val principal: String,
    val students: Int,
    val teachers: Int,
    val status: String,
    val plan: String,
    val state: String,
    val logoUrl: String? = null,
)

data class SchoolsUiState(
    val schools: List<SchoolDetail> = emptyList(),
    val isLoading: Boolean = false,
    val isCreatingSchool: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val searchQuery: String = "",
    val statusFilter: String = "All Statuses",
    val stateFilter: String = "All States",
    val boardFilter: String = "All Boards",
    val planFilter: String = "All Plans",
    val sortOption: String = "None",
    val currentPage: Int = 1,
    val totalSchools: Int = 0,
    val rowsPerPage: Int = 10,
    val isAddSchoolDialogVisible: Boolean = false,
    val isEditSchoolDialogVisible: Boolean = false,
    val editingSchool: SchoolDto? = null
)

class SchoolsViewModel(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SchoolsUiState())
    val uiState: StateFlow<SchoolsUiState> = _uiState.asStateFlow()

    private var allSchools: List<SchoolDetail> = emptyList()

    init {
        loadSchools()
    }

    fun loadSchools() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            schoolRepository.getAllSchools().onSuccess { schools ->
                allSchools = schools.map { it.toDetail() }
                applyFilters()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    private fun SchoolDto.toDetail() = SchoolDetail(
        id = id.toString(),
        name = schoolName,
        code = schoolCode,
        principal = "N/A", // Not available in current DTO
        students = studentCount,
        teachers = teacherCount,
        status = status.name,
        plan = "Professional", // Mocked as not in DTO
        state = state,
        logoUrl = logoUrl,
    )

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query, currentPage = 1)
        applyFilters()
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery.lowercase()
        val status = _uiState.value.statusFilter
        val state = _uiState.value.stateFilter
        val sort = _uiState.value.sortOption

        var filtered = allSchools.filter {
            (it.name.lowercase().contains(query) ||
                    it.code.lowercase().contains(query) ||
                    it.principal.lowercase().contains(query)) &&
                    (status == "All Statuses" || it.status == status.uppercase()) &&
                    (state == "All States" || it.state == state)
        }

        // Sorting
        filtered = when (sort) {
            "Students (High to Low)" -> filtered.sortedByDescending { it.students }
            "Students (Low to High)" -> filtered.sortedBy { it.students }
            "Name (A-Z)" -> filtered.sortedBy { it.name }
            else -> filtered
        }

        val total = filtered.size
        
        // Pagination
        val rowsPerPage = _uiState.value.rowsPerPage
        val currentPage = _uiState.value.currentPage
        val startIndex = (currentPage - 1) * rowsPerPage
        val paginated = filtered.drop(startIndex).take(rowsPerPage)

        _uiState.value = _uiState.value.copy(
            schools = paginated,
            totalSchools = total,
            isLoading = false
        )
    }

    fun onStatusFilterChanged(status: String) {
        _uiState.value = _uiState.value.copy(statusFilter = status, currentPage = 1)
        applyFilters()
    }

    fun onStateFilterChanged(state: String) {
        _uiState.value = _uiState.value.copy(stateFilter = state, currentPage = 1)
        applyFilters()
    }

    fun onBoardFilterChanged(board: String) {
        _uiState.value = _uiState.value.copy(boardFilter = board, currentPage = 1)
        applyFilters()
    }

    fun onPlanFilterChanged(plan: String) {
        _uiState.value = _uiState.value.copy(planFilter = plan, currentPage = 1)
        applyFilters()
    }

    fun onSortOptionChanged(sort: String) {
        _uiState.value = _uiState.value.copy(sortOption = sort, currentPage = 1)
        applyFilters()
    }

    fun onPageChanged(page: Int) {
        _uiState.value = _uiState.value.copy(currentPage = page)
        applyFilters()
    }

    fun onRowsPerPageChanged(rows: Int) {
        _uiState.value = _uiState.value.copy(rowsPerPage = rows, currentPage = 1)
        applyFilters()
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            statusFilter = "All Statuses",
            stateFilter = "All States",
            boardFilter = "All Boards",
            planFilter = "All Plans",
            sortOption = "None",
            currentPage = 1
        )
        applyFilters()
    }

    fun showAddSchoolDialog() {
        _uiState.value = _uiState.value.copy(isAddSchoolDialogVisible = true)
    }

    fun hideAddSchoolDialog() {
        _uiState.value = _uiState.value.copy(isAddSchoolDialogVisible = false)
    }

    fun showEditSchoolDialog(schoolId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            schoolRepository.getSchoolById(schoolId.toLong()).onSuccess { school ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isEditSchoolDialogVisible = true,
                    editingSchool = school
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to fetch school details"
                )
            }
        }
    }

    fun hideEditSchoolDialog() {
        _uiState.value = _uiState.value.copy(isEditSchoolDialogVisible = false, editingSchool = null)
    }

    fun createSchool(request: CreateSchoolRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingSchool = true, error = null)
            schoolRepository.createSchool(request).onSuccess {
                _uiState.value = _uiState.value.copy(
                    isCreatingSchool = false,
                    isAddSchoolDialogVisible = false,
                    successMessage = "School created successfully!"
                )
                loadSchools()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isCreatingSchool = false,
                    error = e.message ?: "Failed to create school"
                )
            }
        }
    }

    fun updateSchoolStatus(schoolId: String, status: SchoolStatus) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            schoolRepository.updateSchool(schoolId.toLong(), UpdateSchoolRequest(status = status))
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "School status updated successfully!"
                    )
                    loadSchools()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to update school status"
                    )
                }
        }
    }

    fun updateSchool(schoolId: String, request: UpdateSchoolRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            schoolRepository.updateSchool(schoolId.toLong(), request)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "School updated successfully!",
                        isEditSchoolDialogVisible = false,
                        editingSchool = null
                    )
                    loadSchools()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to update school"
                    )
                }
        }
    }

    fun deleteSchool(schoolId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            schoolRepository.deleteSchool(schoolId.toLong())
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "School deleted successfully!"
                    )
                    loadSchools()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to delete school"
                    )
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}
