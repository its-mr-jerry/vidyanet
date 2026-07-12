@file:Suppress("DEPRECATION")

package com.kastack.vidyanet.school.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kastack.vidyanet.commonUi.components.AppPagination
import com.kastack.vidyanet.commonUi.components.AppText
import com.kastack.vidyanet.models.settings.BackupSnapshotDto
import com.kastack.vidyanet.models.settings.BackupStorageProvider
import com.kastack.vidyanet.school.components.AdaptiveIconButton
import com.kastack.vidyanet.school.components.HeaderAction
import com.kastack.vidyanet.school.components.SchoolSettingsHeader
import com.kastack.vidyanet.school.components.StatusBadge
import com.kastack.vidyanet.school.viewModels.BackupRestoreViewModel
import com.kastack.vidyanet.theme.*
import com.kastack.vidyanet.utils.toKotlinx
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BackupRestore(
    viewModel: BackupRestoreViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Page Header
        SchoolSettingsHeader(
            title = "Backup & Restore",
            subtitle = "Manage system data backups and restoration points to ensure business continuity and institutional data integrity.",
            breadcrumbs = listOf("Settings", "Backup & Restore"),
            primaryAction = HeaderAction(
                label = "Create Backup",
                icon = Icons.Default.AddCircle,
                onClick = viewModel::startInstantBackup,
                isLoading = uiState.isBackingUp
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Adaptive Bento Grid Layout
            BoxWithConstraints {
                if (maxWidth > 1000.dp) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        // Card 1: Automatic Backups
                        Column(modifier = Modifier.weight(0.35f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            AutomaticBackupsSection(
                                config = uiState.config,
                                lastSuccessful = uiState.lastSuccessfulBackup,
                                onDailyToggle = viewModel::updateDailyBackup,
                                onProviderSelect = viewModel::updateStorageProvider
                            )
                        }

                        // Card 2: Manual Backup
                        Column(modifier = Modifier.weight(0.65f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            ManualBackupSection(
                                isBackingUp = uiState.isBackingUp,
                                progress = uiState.backupProgress,
                                statusText = uiState.backupStatusText,
                                onStartBackup = viewModel::startInstantBackup
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        ManualBackupSection(
                            isBackingUp = uiState.isBackingUp,
                            progress = uiState.backupProgress,
                            statusText = uiState.backupStatusText,
                            onStartBackup = viewModel::startInstantBackup
                        )
                        AutomaticBackupsSection(
                            config = uiState.config,
                            lastSuccessful = uiState.lastSuccessfulBackup,
                            onDailyToggle = viewModel::updateDailyBackup,
                            onProviderSelect = viewModel::updateStorageProvider
                        )
                    }
                }
            }

            // Card 3: Recent Backup History
            RecentBackupHistorySection(
                snapshots = uiState.snapshots,
                totalCount = uiState.totalSnapshots,
                currentPage = uiState.currentPage,
                rowsPerPage = uiState.rowsPerPage,
                onPageChange = viewModel::onPageChanged,
                onRowsPerPageChange = viewModel::onRowsPerPageChanged,
                onRestore = viewModel::restoreSnapshot,
                onDelete = viewModel::deleteSnapshot
            )
        }
    }
}


@Composable
private fun AutomaticBackupsSection(
    config: com.kastack.vidyanet.models.settings.BackupConfigDto,
    lastSuccessful: String,
    onDailyToggle: (Boolean) -> Unit,
    onProviderSelect: (BackupStorageProvider) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AutoMode, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(24.dp))
                }
                AppText("Automatic Backups", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BackupScheduleRow("Daily Backups", "Scheduled at 02:00 AM EST", config.dailyEnabled, onDailyToggle)
                BackupScheduleRow("Weekly Backups", "Scheduled every Sunday", config.weeklyEnabled, {}, enabled = false)
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AppText("Cloud Storage Destination", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StorageProviderButton("AWS S3", Icons.Default.Cloud, config.storageProvider == BackupStorageProvider.AWS_S3, { onProviderSelect(BackupStorageProvider.AWS_S3) }, Modifier.weight(1f))
                    StorageProviderButton("G-Drive", Icons.Default.CloudQueue, config.storageProvider == BackupStorageProvider.GOOGLE_DRIVE, { onProviderSelect(BackupStorageProvider.GOOGLE_DRIVE) }, Modifier.weight(1f))
                    StorageProviderButton("Dropbox", Icons.Default.FolderShared, config.storageProvider == BackupStorageProvider.DROPBOX, { onProviderSelect(BackupStorageProvider.DROPBOX) }, Modifier.weight(1f))
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                AppText("Last Successful Backup: ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                AppText(lastSuccessful, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BackupScheduleRow(title: String, subtitle: String, checked: Boolean, onToggle: (Boolean) -> Unit, enabled: Boolean = true) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                AppText(title, style = MaterialTheme.typography.labelLarge, color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                AppText(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (enabled) 1f else 0.5f))
            }
            Switch(checked = checked, onCheckedChange = onToggle, enabled = enabled)
        }
    }
}

@Composable
private fun StorageProviderButton(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, null, tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            AppText(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ManualBackupSection(
    isBackingUp: Boolean,
    progress: Float,
    statusText: String,
    onStartBackup: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(1f).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                        .border(8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Backup, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                }
                
                Spacer(Modifier.height(24.dp))
                AppText("Create Instant Backup", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                AppText(
                    "Initiate a complete snapshot of all district databases, student records, and system configurations immediately.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 400.dp).padding(top = 8.dp)
                )
                
                Spacer(Modifier.height(32.dp))
                
                if (!isBackingUp) {
                    Button(
                        onClick = onStartBackup,
                        modifier = Modifier.width(320.dp).height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, null)
                        Spacer(Modifier.width(12.dp))
                        AppText("Start Snapshot", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Column(modifier = Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            AppText(statusText, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            AppText("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    }
                }
            }

            // Safety Warning
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AcademicError.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, color = AcademicError.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(Icons.Default.Warning, null, tint = AcademicError)
                    Column {
                        AppText("Data Preservation Warning", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = AcademicError)
                        AppText(
                            "Restoring a backup point will permanently overwrite all data generated after that snapshot was taken. This action cannot be undone. Ensure a fresh backup is created before performing any restoration procedure.",
                            style = MaterialTheme.typography.labelSmall,
                            color = AcademicError.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentBackupHistorySection(
    snapshots: List<BackupSnapshotDto>,
    totalCount: Int,
    currentPage: Int,
    rowsPerPage: Int,
    onPageChange: (Int) -> Unit,
    onRowsPerPageChange: (Int) -> Unit,
    onRestore: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f)).padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.History, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    AppText("Recent Backup History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { AppText("Search backups...", style = MaterialTheme.typography.bodySmall) },
                        leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.width(240.dp).height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.surface)
                    )
                    OutlinedButton(onClick = {}, shape = RoundedCornerShape(8.dp)) {
                        Icon(Icons.Default.FilterList, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        AppText("Filter", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            // Table Header
            BoxWithConstraints {
                val viewportWidth = maxWidth
                Column(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    val tableWidth = if (viewportWidth > 900.dp) viewportWidth else 900.dp
                    Column(modifier = Modifier.width(tableWidth)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppText("SNAPSHOT DETAILS", Modifier.weight(1.5f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            AppText("SIZE", Modifier.weight(0.8f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            AppText("CREATED BY", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            AppText("STORAGE LOCATION", Modifier.weight(1.2f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(150.dp))
                        }
                        
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        
                        BoxWithConstraints {
                            val isMobile = maxWidth < 700.dp
                            if (isMobile) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    snapshots.forEach { snapshot ->
                                        BackupSnapshotMobileCard(snapshot, onRestore, onDelete)
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                    }
                                }
                            } else {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    snapshots.forEach { snapshot ->
                                        BackupSnapshotRow(snapshot, onRestore, onDelete)
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Pagination
            AppPagination(
                totalItems = totalCount,
                currentPage = currentPage,
                rowsPerPage = rowsPerPage,
                onPageChange = onPageChange,
                onRowsPerPageChange = onRowsPerPageChange
            )
        }
    }
}

@Composable
private fun BackupSnapshotMobileCard(snapshot: BackupSnapshotDto, onRestore: (String) -> Unit, onDelete: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val dt = snapshot.timestamp.toKotlinx().toLocalDateTime(TimeZone.currentSystemDefault())
        val dateStr = "${dt.month.name.take(3)} ${dt.day}, ${dt.year} · ${dt.hour.toString().padStart(2, '0')}:${dt.minute.toString().padStart(2, '0')} ${if (dt.hour >= 12) "PM" else "AM"}"

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                AppText(snapshot.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                AppText(dateStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusBadge(text = snapshot.size, color = MaterialTheme.colorScheme.outline)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                Icon(if (snapshot.isAutoGenerated) Icons.Default.SmartToy else Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                AppText(snapshot.createdBy, style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.CloudQueue, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                AppText(snapshot.storageLocation, style = MaterialTheme.typography.bodySmall)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onRestore(snapshot.id) }) { Icon(Icons.Default.SettingsBackupRestore, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary) }
            IconButton(onClick = { }) { Icon(Icons.Default.Download, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            IconButton(onClick = { onDelete(snapshot.id) }) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(20.dp), tint = AcademicError) }
        }
    }
}

@Composable
private fun BackupSnapshotRow(snapshot: BackupSnapshotDto, onRestore: (String) -> Unit, onDelete: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val dt = snapshot.timestamp.toKotlinx().toLocalDateTime(TimeZone.currentSystemDefault())
        val dateStr = "${dt.month.name.take(3)} ${dt.day}, ${dt.year} · ${dt.hour.toString().padStart(2, '0')}:${dt.minute.toString().padStart(2, '0')} ${if (dt.hour >= 12) "PM" else "AM"}"

        Column(Modifier.weight(1.5f)) {
            AppText(snapshot.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            AppText(dateStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        Box(Modifier.weight(0.8f)) {
            Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh, shape = RoundedCornerShape(4.dp)) {
                AppText(snapshot.size, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
        }
        
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(if (snapshot.isAutoGenerated) Icons.Default.SmartToy else Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            AppText(snapshot.createdBy, style = MaterialTheme.typography.bodySmall)
        }
        
        Row(modifier = Modifier.weight(1.2f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.CloudQueue, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            AppText(snapshot.storageLocation, style = MaterialTheme.typography.bodySmall)
        }
        
        Row(modifier = Modifier.width(150.dp), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { onRestore(snapshot.id) }) { Icon(Icons.Default.SettingsBackupRestore, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary) }
            IconButton(onClick = { }) { Icon(Icons.Default.Download, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            IconButton(onClick = { onDelete(snapshot.id) }) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp), tint = AcademicError) }
        }
    }
}
