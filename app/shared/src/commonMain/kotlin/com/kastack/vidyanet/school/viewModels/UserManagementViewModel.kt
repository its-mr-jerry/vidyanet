@file:Suppress("DEPRECATION")

package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.core.GlobalStore
import com.kastack.vidyanet.data.repositories.RoleRepository
import com.kastack.vidyanet.data.repositories.UserRepository
import com.kastack.vidyanet.models.user.*
import com.kastack.vidyanet.models.role.RoleDto
import com.kastack.vidyanet.permissions.PermissionSchema
import com.kastack.vidyanet.validators.UserValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class UserUiModel(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val roleIds: List<Long>,
    val lastLogin: String,
    val status: UserStatus,
    val avatarUrl: String? = null
)

data class UserManagementUiState(
    val users: List<UserUiModel> = emptyList(),
    val roles: List<RoleDto> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val canEdit: Boolean = false,
    val error: String? = null,
    val totalUsers: Int = 0,
    val currentPage: Int = 1,
    val rowsPerPage: Int = 10,
    val searchQuery: String = "",
    val selectedRoleId: Long? = null,
    val selectedStatus: UserStatus? = null,
    val editingUser: UserUiModel? = null
)

class UserManagementViewModel(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val globalStore: GlobalStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserManagementUiState())
    val uiState: StateFlow<UserManagementUiState> = _uiState.asStateFlow()
    private var currentSchoolId: String? = null

    fun init(schoolId: String) {
        currentSchoolId = schoolId
        _uiState.value = _uiState.value.copy(canEdit = globalStore.hasPermission(PermissionSchema.Settings.MODULE, "EDIT"))
        loadData()
    }

    private fun loadData() {
        if (currentSchoolId?.toLongOrNull() == null) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val rolesResult = roleRepository.getAllRoles()
            _uiState.value = _uiState.value.copy(roles = rolesResult.getOrDefault(emptyList()))

            loadUsers()
        }
    }

    fun loadUsers() {
        val schoolId = currentSchoolId?.toLongOrNull() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            userRepository.getAllUsers(
                search = _uiState.value.searchQuery.takeIf { it.isNotBlank() },
                roleId = _uiState.value.selectedRoleId,
                status = _uiState.value.selectedStatus,
                schoolId = schoolId,
                page = _uiState.value.currentPage,
                pageSize = _uiState.value.rowsPerPage
            ).onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    users = response.items.map { it.toUiModel() },
                    totalUsers = response.totalItems.toInt(),
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

    private fun UserDto.toUiModel() = UserUiModel(
        id = id,
        name = fullName ?: "Unknown",
        email = email ?: "",
        phone = phone,
        role = roles.joinToString(", "),
        roleIds = roleIds,
        lastLogin = lastLoginAt?.toString() ?: "Never",
        status = status
    )

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query, currentPage = 1)
        loadUsers()
    }

    fun onRoleFilterChanged(roleId: Long?) {
        _uiState.value = _uiState.value.copy(selectedRoleId = roleId, currentPage = 1)
        loadUsers()
    }

    fun onStatusFilterChanged(status: UserStatus?) {
        _uiState.value = _uiState.value.copy(selectedStatus = status, currentPage = 1)
        loadUsers()
    }

    fun onPageChanged(page: Int) {
        _uiState.value = _uiState.value.copy(currentPage = page)
        loadUsers()
    }

    fun onRowsPerPageChanged(rows: Int) {
        _uiState.value = _uiState.value.copy(rowsPerPage = rows, currentPage = 1)
        loadUsers()
    }

    fun createUser(fullName: String, phone: String, email: String, roleIds: List<Long>) {
        val schoolId = currentSchoolId?.toLongOrNull() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            val request = CreateUserRequest(
                phone = phone,
                fullName = fullName,
                email = email,
                userType = UserType.SCHOOL_USER,
                schoolId = schoolId,
                roleIds = roleIds
            )
            
            try {
                UserValidator.validateCreate(request)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
                return@launch
            }

            userRepository.createUser(request).onSuccess {
                _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
                loadUsers()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isSaving = false, error = error.message)
            }
        }
    }

    fun updateUser(userId: Long, fullName: String, email: String, roleIds: List<Long>) {
        if (!_uiState.value.canEdit) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            val request = UpdateUserRequest(
                fullName = fullName,
                email = email,
                roleIds = roleIds
            )

            userRepository.updateUser(userId, request).onSuccess {
                _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true, editingUser = null)
                loadUsers()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isSaving = false, error = error.message)
            }
        }
    }

    fun setEditingUser(user: UserUiModel?) {
        _uiState.value = _uiState.value.copy(editingUser = user)
    }

    fun updateUserStatus(user: UserUiModel, newStatus: UserStatus) {
        if (!_uiState.value.canEdit) return
        viewModelScope.launch {
            userRepository.updateUser(user.id, UpdateUserRequest(status = newStatus)).onSuccess {
                loadUsers()
            }
        }
    }

    fun deleteUser(userId: Long) {
        if (!_uiState.value.canEdit) return
        viewModelScope.launch {
            userRepository.deleteUser(userId).onSuccess {
                loadUsers()
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun importUsersFromCsv(csvData: String) {
        val schoolId = currentSchoolId?.toLongOrNull() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            val lines = csvData.lines().filter { it.isNotBlank() }
            if (lines.size <= 1) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = "CSV is empty or only contains header")
                return@launch
            }

            val header = lines.first().split(",").map { it.trim().lowercase() }
            val nameIdx = header.indexOf("full name")
            val phoneIdx = header.indexOf("phone")
            val emailIdx = header.indexOf("email")
            val rolesIdx = header.indexOf("roles")

            if (nameIdx == -1 || phoneIdx == -1) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = "Invalid CSV format. Required columns: Full Name, Phone")
                return@launch
            }

            var successCount = 0
            var failCount = 0

            lines.drop(1).forEach { line ->
                val columns = line.split(",").map { it.trim() }
                if (columns.size > maxOf(nameIdx, phoneIdx)) {
                    val fullName = columns[nameIdx]
                    val phone = columns[phoneIdx]
                    val email = if (emailIdx != -1 && columns.size > emailIdx) columns[emailIdx] else ""
                    val rolesStr = if (rolesIdx != -1 && columns.size > rolesIdx) columns[rolesIdx] else ""
                    
                    val roleIds = if (rolesStr.contains(";")) {
                        rolesStr.split(";").mapNotNull { it.trim().toLongOrNull() }
                    } else {
                        // Support comma or semicolon
                        rolesStr.split(",").mapNotNull { it.trim().toLongOrNull() }
                    }

                    val request = CreateUserRequest(
                        phone = phone,
                        fullName = fullName,
                        email = email.takeIf { it.isNotBlank() },
                        userType = UserType.SCHOOL_USER,
                        schoolId = schoolId,
                        roleIds = roleIds
                    )

                    userRepository.createUser(request).onSuccess {
                        successCount++
                    }.onFailure {
                        failCount++
                    }
                }
            }

            _uiState.value = _uiState.value.copy(
                isSaving = false,
                saveSuccess = true,
                error = if (failCount > 0) "Imported $successCount users. Failed $failCount users." else null
            )
            loadUsers()
        }
    }
    
    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}
