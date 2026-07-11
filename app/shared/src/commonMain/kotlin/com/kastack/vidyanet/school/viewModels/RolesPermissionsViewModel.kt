@file:Suppress("DEPRECATION")

package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.models.role.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.milliseconds

data class RolesPermissionsUiState(
    val roles: List<RoleDto> = emptyList(),
    val selectedRole: RoleDto? = null,
    val permissions: List<ModulePermissionDto> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

class RolesPermissionsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RolesPermissionsUiState())
    val uiState: StateFlow<RolesPermissionsUiState> = _uiState.asStateFlow()

    init {
        loadRoles()
    }

    private fun loadRoles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(500.milliseconds) // Simulate network
            val mockRoles = listOf(
                RoleDto(1, "SCHOOL_ADMIN", "School Admin", "Full system access", true, Clock.System.now(), Clock.System.now()),
                RoleDto(2, "TEACHER", "Teacher", "Custom access for teachers", false, Clock.System.now(), Clock.System.now()),
                RoleDto(3, "ACCOUNTANT", "Accountant", "Financial management", false, Clock.System.now(), Clock.System.now()),
                RoleDto(4, "LIBRARIAN", "Librarian", "Resource management", false, Clock.System.now(), Clock.System.now())
            )
            _uiState.value = _uiState.value.copy(
                roles = mockRoles,
                selectedRole = mockRoles.find { it.roleCode == "TEACHER" },
                permissions = getMockPermissions(),
                isLoading = false
            )
        }
    }

    fun selectRole(role: RoleDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedRole = role, isLoading = true)
            delay(300.milliseconds)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun togglePermission(moduleName: String, action: PermissionAction) {
        val currentPermissions = _uiState.value.permissions.toMutableList()
        val index = currentPermissions.indexOfFirst { it.moduleName == moduleName }
        if (index != -1) {
            val module = currentPermissions[index]
            val newActions = if (module.actions.contains(action)) {
                module.actions - action
            } else {
                module.actions + action
            }
            currentPermissions[index] = module.copy(actions = newActions)
            _uiState.value = _uiState.value.copy(permissions = currentPermissions)
        }
    }

    fun savePermissions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            delay(1000)
            _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
            delay(3000)
            _uiState.value = _uiState.value.copy(saveSuccess = false)
        }
    }

    private fun getMockPermissions() = listOf(
        ModulePermissionDto("Dashboard", "Analytics and stats overview", setOf(PermissionAction.VIEW, PermissionAction.EXPORT)),
        ModulePermissionDto("Student Records", "Profiles, grades, and discipline", setOf(PermissionAction.VIEW, PermissionAction.CREATE, PermissionAction.EDIT, PermissionAction.DELETE, PermissionAction.EXPORT)),
        ModulePermissionDto("Attendance", "Daily marking and monthly reports", setOf(PermissionAction.VIEW, PermissionAction.CREATE, PermissionAction.EDIT, PermissionAction.EXPORT)),
        ModulePermissionDto("Examinations", "Results entry and exam scheduling", setOf(PermissionAction.VIEW, PermissionAction.CREATE, PermissionAction.EXPORT)),
        ModulePermissionDto("Library", "Book search and requests only", setOf(PermissionAction.VIEW, PermissionAction.CREATE))
    )
}
