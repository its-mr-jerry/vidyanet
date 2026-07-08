package com.kastack.vidyanet.superAdmin.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardKpi(
    val label: String,
    val value: String,
    val trend: String,
    val isError: Boolean = false
)

data class SystemHealthState(
    val cpuUsage: Int = 0,
    val ramUsed: String = "0/0 GB",
    val storageUsed: String = "0/0 GB",
    val dbStatus: String = "Healthy"
)

data class SuperAdminDashboardUiState(
    val kpis: List<DashboardKpi> = emptyList(),
    val systemHealth: SystemHealthState = SystemHealthState(),
    val academicYear: String = "2023-24"
)

class SuperAdminDashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SuperAdminDashboardUiState())
    val uiState: StateFlow<SuperAdminDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        _uiState.value = SuperAdminDashboardUiState(
            kpis = listOf(
                DashboardKpi("Total Schools", "124", "+8%"),
                DashboardKpi("Total Students", "45,230", "+12%"),
                DashboardKpi("Total Teachers", "3,450", "+5%"),
                DashboardKpi("Active Staff", "1,890", "98% Uptime"),
                DashboardKpi("Monthly Revenue", "$450,000", "+15%"),
                DashboardKpi("Pending Payments", "$12,500", "-5%", isError = true),
                DashboardKpi("New Admissions", "1,240", "This Month"),
                DashboardKpi("Overall Uptime", "99.9%", "Optimal")
            ),
            systemHealth = SystemHealthState(
                cpuUsage = 42,
                ramUsed = "10/16 GB",
                storageUsed = "120/500 GB",
                dbStatus = "Online"
            )
        )
    }
}
