@file:Suppress("DEPRECATION")

package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.data.repositories.RoleRepository
import com.kastack.vidyanet.models.role.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RolesPermissionsUiState(
    val roles: List<RoleDto> = emptyList(),
    val selectedRole: RoleDto? = null,
    val permissions: List<ModulePermissionDto> = emptyList(),
    val availablePermissions: List<ModulePermissionDto> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

class RolesPermissionsViewModel(
    private val roleRepository: RoleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RolesPermissionsUiState())
    val uiState: StateFlow<RolesPermissionsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val rolesResult = roleRepository.getAllRoles()
            val permissionsResult = roleRepository.getAllPermissions()

            _uiState.value = _uiState.value.copy(
                roles = rolesResult.getOrDefault(emptyList()),
                availablePermissions = permissionsResult.getOrDefault(emptyList()),
                isLoading = false,
                error = rolesResult.exceptionOrNull()?.message ?: permissionsResult.exceptionOrNull()?.message
            )

            // Auto-select first role if available
            _uiState.value.roles.firstOrNull()?.let { selectRole(it) }
        }
    }

    fun selectRole(role: RoleDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedRole = role, isLoading = true)
            
            roleRepository.getRolePermissions(role.id).onSuccess { rolePerms ->
                // Merge with available permissions to show all modules
                val mergedPermissions = _uiState.value.availablePermissions.map { available ->
                    val activeActions = rolePerms.permissions.find { it.moduleName == available.moduleName }?.actions ?: emptySet()
                    available.copy(actions = activeActions)
                }
                
                _uiState.value = _uiState.value.copy(
                    permissions = mergedPermissions,
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
        val selectedRole = _uiState.value.selectedRole ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            
            val request = RolePermissionsDto(
                roleId = selectedRole.id,
                permissions = _uiState.value.permissions
            )
            
            roleRepository.updateRolePermissions(selectedRole.id, request).onSuccess {
                _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isSaving = false, error = error.message)
            }
        }
    }

    fun createRole(name: String, code: String, description: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val request = CreateRoleRequest(
                roleCode = code.uppercase(),
                roleName = name,
                description = description,
                isSystemRole = false
            )
            roleRepository.createRole(request).onSuccess {
                _uiState.value = _uiState.value.copy(isSaving = false)
                loadData()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isSaving = false, error = error.message)
            }
        }
    }

    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
