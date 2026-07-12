@file:Suppress("DEPRECATION")

package com.kastack.vidyanet.school.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastack.vidyanet.models.settings.*
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

data class BackupRestoreUiState(
    val config: BackupConfigDto = BackupConfigDto(),
    val snapshots: List<BackupSnapshotDto> = emptyList(),
    val isLoading: Boolean = false,
    val isBackingUp: Boolean = false,
    val backupProgress: Float = 0f,
    val backupStatusText: String = "",
    val error: String? = null,
    val totalSnapshots: Int = 0,
    val currentPage: Int = 1,
    val rowsPerPage: Int = 10,
    val lastSuccessfulBackup: String = "2 hours ago"
)

class BackupRestoreViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BackupRestoreUiState())
    val uiState: StateFlow<BackupRestoreUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(500.milliseconds)
            
            val now = Clock.System.now().toKotlin()
            val mockSnapshots = listOf(
                BackupSnapshotDto(
                    "1", "District_Master_Snap_2023_10_24", 
                    now.minus(2.hours), "2.44 GB", "System (Auto)", "AWS S3 (Virginia)", true
                ),
                BackupSnapshotDto(
                    "2", "Manual_Pre_Migration_Update", 
                    now.minus(12.hours), "1.82 GB", "admin_robert_w", "Local Storage (Node-02)", false
                ),
                BackupSnapshotDto(
                    "3", "District_Master_Snap_2023_10_23", 
                    now.minus(1.days), "2.41 GB", "System (Auto)", "AWS S3 (Virginia)", true
                )
            )
            
            _uiState.value = _uiState.value.copy(
                snapshots = mockSnapshots,
                totalSnapshots = 48,
                isLoading = false
            )
        }
    }

    fun onPageChanged(page: Int) {
        _uiState.value = _uiState.value.copy(currentPage = page)
        loadData()
    }

    fun onRowsPerPageChanged(rows: Int) {
        _uiState.value = _uiState.value.copy(rowsPerPage = rows, currentPage = 1)
        loadData()
    }

    fun startInstantBackup() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isBackingUp = true, 
                backupProgress = 0f, 
                backupStatusText = "Gathering Database Metadata..."
            )
            
            val steps = listOf(
                "Gathering Database Metadata..." to 0.1f,
                "Compressing School Records..." to 0.3f,
                "Encrypting Snapshot (AES-256)..." to 0.6f,
                "Uploading to Cloud Storage..." to 0.9f,
                "Finalizing Integrity Check..." to 1.0f
            )

            for (step in steps) {
                _uiState.value = _uiState.value.copy(backupStatusText = step.first)
                val targetProgress = step.second
                while (_uiState.value.backupProgress < targetProgress) {
                    delay(100.milliseconds)
                    val nextProgress = (_uiState.value.backupProgress + 0.05f).coerceAtMost(targetProgress)
                    _uiState.value = _uiState.value.copy(backupProgress = nextProgress)
                }
            }
            
            _uiState.value = _uiState.value.copy(backupStatusText = "Backup Complete!")
            delay(1000.milliseconds)
            _uiState.value = _uiState.value.copy(isBackingUp = false, backupProgress = 0f)
            loadData() // Refresh list
        }
    }

    fun updateDailyBackup(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(config = _uiState.value.config.copy(dailyEnabled = enabled))
    }

    fun updateStorageProvider(provider: BackupStorageProvider) {
        _uiState.value = _uiState.value.copy(config = _uiState.value.config.copy(storageProvider = provider))
    }

    fun restoreSnapshot(snapshotId: String) {
        // Implementation for restore
    }

    fun deleteSnapshot(snapshotId: String) {
        // Implementation for delete
    }
}
