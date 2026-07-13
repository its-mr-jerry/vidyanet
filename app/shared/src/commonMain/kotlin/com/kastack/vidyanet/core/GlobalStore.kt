package com.kastack.vidyanet.core


import com.kastack.vidyanet.models.system.SystemConfigDto
import com.kastack.vidyanet.models.user.UserDto
import com.kastack.vidyanet.models.user.UserType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class GlobalStore {
    private val _currentUser = MutableStateFlow<UserDto?>(null)
    val currentUser: StateFlow<UserDto?> = _currentUser.asStateFlow()

    private val _adminName = MutableStateFlow("Super Admin")
    val adminName: StateFlow<String> = _adminName.asStateFlow()
    
    private val _systemConfig = MutableStateFlow<SystemConfigDto?>(null)
    val systemConfig = _systemConfig.asStateFlow()

    val isSystemOnline = _systemConfig.map { it?.isMaintenanceMode?.not() ?: true }

    fun updateCurrentUser(user: UserDto) {
        _currentUser.value = user
        _adminName.value = user.fullName ?: "User"
    }

    fun hasPermission(module: String, action: String): Boolean {
        val user = _currentUser.value ?: return false
        if (user.userType == UserType.PLATFORM_OWNER) return true
        
        return user.permissions.contains("${module.uppercase()}_${action.uppercase()}")
    }

    fun updateAdminName(name: String) {
        _adminName.value = name
    }

    fun updateSystemConfig(config: SystemConfigDto) {
        _systemConfig.value = config
    }

    fun clear() {
        _currentUser.value = null
        _adminName.value = "Super Admin"
        _systemConfig.value = null
    }
}

