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
                DashboardKpi("Monthly Revenue", "₹4,50,000", "+15%"),
                DashboardKpi("Active Sessions", "3,240", "Real-time"),
                DashboardKpi("Avg. API Latency", "124ms", "Optimal"),
                DashboardKpi("Total Students", "45,230", "Global Scale"),
                DashboardKpi("Subscription Renewals", "12", "Next 30 Days"),
                DashboardKpi("Pending Approvals", "5", "Action Required", isError = true),
                DashboardKpi("Platform Uptime", "99.99%", "Optimal")
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
