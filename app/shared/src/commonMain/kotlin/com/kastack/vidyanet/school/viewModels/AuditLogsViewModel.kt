@file:Suppress("DEPRECATION")

package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.models.audit.AuditLogDto
import com.kastack.vidyanet.models.audit.AuditStatus
import com.kastack.vidyanet.utils.toKotlin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

data class AuditLogsUiState(
    val logs: List<AuditLogDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalEntries: Int = 0,
    val currentPage: Int = 1,
    val pageSize: Int = 10
)

class AuditLogsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuditLogsUiState())
    val uiState: StateFlow<AuditLogsUiState> = _uiState.asStateFlow()

    init {
        loadLogs()
    }

    fun loadLogs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(500.milliseconds) // Simulate network
            
            val now = Clock.System.now().toKotlin()
            val mockLogs = listOf(
                AuditLogDto(
                    id = 1,
                    timestamp = now.minus(2.hours),
                    userId = 101,
                    userName = "Sarah Adams",
                    userRole = "Super Admin",
                    action = "Updated School Settings",
                    actionDetails = "Changed fiscal year end date",
                    module = "Settings",
                    status = AuditStatus.INFO,
                    ipAddress = "192.168.1.104"
                ),
                AuditLogDto(
                    id = 2,
                    timestamp = now.minus(4.hours),
                    userId = 102,
                    userName = "Mark Chen",
                    userRole = "Finance Officer",
                    action = "Bulk Invoice Generation",
                    actionDetails = "1,240 invoices created",
                    module = "Finance",
                    status = AuditStatus.INFO,
                    ipAddress = "172.16.254.1"
                ),
                AuditLogDto(
                    id = 3,
                    timestamp = now.minus(8.hours),
                    userId = 0,
                    userName = "System Process",
                    userRole = "Auto-Task",
                    action = "Failed Backup Attempt",
                    actionDetails = "Disk space limit reached",
                    module = "System",
                    status = AuditStatus.CRITICAL,
                    ipAddress = "Localhost"
                ),
                AuditLogDto(
                    id = 4,
                    timestamp = now.minus(1.days),
                    userId = 105,
                    userName = "John Doe",
                    userRole = "Registrar",
                    action = "Access Denied",
                    actionDetails = "Unauthorized access to Payroll",
                    module = "Finance",
                    status = AuditStatus.WARNING,
                    ipAddress = "203.0.113.42"
                )
            )

            _uiState.value = _uiState.value.copy(
                logs = mockLogs,
                totalEntries = 2841,
                isLoading = false
            )
        }
    }

    fun onPageChange(page: Int) {
        _uiState.value = _uiState.value.copy(currentPage = page)
        loadLogs()
    }

    fun onPageSizeChange(size: Int) {
        _uiState.value = _uiState.value.copy(pageSize = size, currentPage = 1)
        loadLogs()
    }
}
