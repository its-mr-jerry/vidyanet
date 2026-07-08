package com.kastack.vidyanet.core


import com.kastack.vidyanet.models.system.SystemConfigDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class GlobalStore {
    private val _adminName = MutableStateFlow("Super Admin")
    val adminName: StateFlow<String> = _adminName.asStateFlow()
    
    private val _systemConfig = MutableStateFlow<SystemConfigDto?>(null)
    val systemConfig = _systemConfig.asStateFlow()

    val isSystemOnline = _systemConfig.map { it?.isMaintenanceMode?.not() ?: true }

    fun updateAdminName(name: String) {
        _adminName.value = name
    }

    fun updateSystemConfig(config: SystemConfigDto) {
        _systemConfig.value = config
    }


}

