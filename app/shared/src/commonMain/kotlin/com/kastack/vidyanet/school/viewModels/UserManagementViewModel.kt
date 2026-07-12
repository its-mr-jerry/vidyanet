@file:Suppress("DEPRECATION")

package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.data.repositories.RoleRepository
import com.kastack.vidyanet.data.repositories.UserRepository
import com.kastack.vidyanet.models.user.*
import com.kastack.vidyanet.models.role.RoleDto
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
    val error: String? = null,
    val totalUsers: Int = 0,
    val currentPage: Int = 1,
    val rowsPerPage: Int = 10,
    val pendingInvites: List<PendingInvite> = emptyList(),
    val searchQuery: String = ""
)

data class PendingInvite(
    val email: String,
    val role: String,
    val sentAt: String
)

class UserManagementViewModel(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserManagementUiState())
    val uiState: StateFlow<UserManagementUiState> = _uiState.asStateFlow()
    private var currentSchoolId: String? = null

    fun init(schoolId: String) {
        currentSchoolId = schoolId
        loadData()
    }

    private fun loadData() {
        val schoolId = currentSchoolId?.toLongOrNull() ?: return
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
        lastLogin = lastLoginAt?.toString() ?: "Never",
        status = status
    )

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query, currentPage = 1)
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
            _uiState.value = _uiState.value.copy(isSaving = true)
            val request = CreateUserRequest(
                phone = phone,
                fullName = fullName,
                email = email,
                userType = UserType.SCHOOL_USER,
                schoolId = schoolId,
                roleIds = roleIds
            )
            userRepository.createUser(request).onSuccess {
                _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
                loadUsers()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isSaving = false, error = error.message)
            }
        }
    }

    fun toggleUserStatus(user: UserUiModel) {
        viewModelScope.launch {
            val newStatus = if (user.status == UserStatus.ACTIVE) UserStatus.INACTIVE else UserStatus.ACTIVE
            userRepository.updateUser(user.id, UpdateUserRequest(status = newStatus)).onSuccess {
                loadUsers()
            }
        }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            userRepository.deleteUser(userId).onSuccess {
                loadUsers()
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}

