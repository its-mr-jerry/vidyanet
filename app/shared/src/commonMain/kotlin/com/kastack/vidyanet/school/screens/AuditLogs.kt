package com.kastack.vidyanet.school.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kastack.vidyanet.commonUi.components.AppPagination
import com.kastack.vidyanet.commonUi.components.AppText
import com.kastack.vidyanet.models.audit.AuditLogDto
import com.kastack.vidyanet.models.audit.AuditStatus
import com.kastack.vidyanet.school.components.HeaderAction
import com.kastack.vidyanet.school.components.SchoolSettingsHeader
import com.kastack.vidyanet.school.components.StatusBadge
import com.kastack.vidyanet.school.viewModels.AuditLogsViewModel
import com.kastack.vidyanet.theme.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toStdlibInstant
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuditLogs(
    viewModel: AuditLogsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Page Title and Actions
        SchoolSettingsHeader(
            title = "Audit Logs",
            subtitle = "Track all administrative actions and system events for security and compliance across the EduCore ERP ecosystem.",
            breadcrumbs = listOf("Settings", "Audit Logs")
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Filter Bar
            AuditFilterBar()

            // Main Content: Audit Table
            AuditTable(
                logs = uiState.logs,
                totalEntries = uiState.totalEntries,
                currentPage = uiState.currentPage,
                pageSize = uiState.pageSize,
                onPageChange = viewModel::onPageChange,
                onPageSizeChange = viewModel::onPageSizeChange
            )
        }
    }
}


@Composable
private fun AuditFilterBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        BoxWithConstraints {
            if (maxWidth > 900.dp) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SearchActionField(Modifier.weight(1f))
                    AuditDropdownField("All Modules", Modifier.width(180.dp), "Module")
                    AuditDropdownField("Oct 01 - Oct 31, 2023", Modifier.width(220.dp), "Date Range", Icons.Default.CalendarToday)
                    ExportButton()
                }
            } else {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SearchActionField(Modifier.fillMaxWidth())
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AuditDropdownField("All Modules", Modifier.weight(1f), "Module")
                        AuditDropdownField("Oct 01 - Oct 31, 2023", Modifier.weight(1f), "Date Range", Icons.Default.CalendarToday)
                    }
                    ExportButton(Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun SearchActionField(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        AppText("Search Action or User", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { AppText("e.g. 'Updated Settings'", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = { Icon(Icons.Default.PersonSearch, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            singleLine = true
        )
    }
}

@Composable
private fun ExportButton(modifier: Modifier = Modifier) {
    IconButton(
        onClick = {},
        modifier = modifier
            .padding(top = 16.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
    ) {
        Icon(Icons.Default.Download, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AuditDropdownField(value: String, modifier: Modifier = Modifier, label: String? = null, icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.ArrowDropDown) {
    Column(modifier = modifier) {
        label?.let {
            AppText(it, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surface,
            onClick = {}
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppText(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun AuditTable(
    logs: List<AuditLogDto>,
    totalEntries: Int,
    currentPage: Int,
    pageSize: Int,
    onPageChange: (Int) -> Unit,
    onPageSizeChange: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        BoxWithConstraints {
            val viewportWidth = maxWidth
            val isMobile = maxWidth < 700.dp
            Column(modifier = Modifier.fillMaxWidth()) {
                if (isMobile) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        logs.forEach { log ->
                            AuditMobileCard(log)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                        // Table Content
                        val tableWidth = if (viewportWidth > 1000.dp) viewportWidth else 1000.dp
                        Column(modifier = Modifier.width(tableWidth)) {
                            // Table Header
                            Row(
                                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLow).padding(horizontal = 24.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppText("TIMESTAMP", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                AppText("USER", Modifier.weight(1.5f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                AppText("ACTION", Modifier.weight(1.5f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                AppText("MODULE", Modifier.weight(0.8f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                AppText("STATUS", Modifier.weight(0.8f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                AppText("IP ADDRESS", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                AppText("DETAILS", Modifier.weight(0.8f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.End)
                            }
                            
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            
                            Column(modifier = Modifier.fillMaxWidth()) {
                                logs.forEach { log ->
                                    AuditLogRow(log)
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                }
                            }
                        }
                    }
                }
                
                // Pagination
                AppPagination(
                    totalItems = totalEntries,
                    currentPage = currentPage,
                    rowsPerPage = pageSize,
                    onPageChange = onPageChange,
                    onRowsPerPageChange = onPageSizeChange
                )
            }
        }
    }
}

@Composable
private fun AuditMobileCard(log: AuditLogDto) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val dt = log.timestamp.toStdlibInstant().toLocalDateTime(TimeZone.currentSystemDefault())
        val dateStr = "${dt.month.name.take(3)} ${dt.day}, ${dt.year} ${dt.hour.toString().padStart(2, '0')}:${dt.minute.toString().padStart(2, '0')}"
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                AppText(dateStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                AppText(log.action, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                AppText(log.actionDetails ?: "", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            val statusColor = when(log.status) {
                AuditStatus.CRITICAL, AuditStatus.FAILURE -> AcademicError
                AuditStatus.WARNING -> AcademicWarning
                else -> MaterialTheme.colorScheme.primary
            }
            StatusBadge(text = log.status.name.lowercase().replaceFirstChar { it.uppercase() }, color = statusColor)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(24.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                    AppText(log.userName.take(1), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
                AppText(log.userName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            }
            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(4.dp)) {
                AppText(log.module.uppercase(), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            AppText("IP: ${log.ipAddress ?: "-"}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            TextButton(onClick = {}, contentPadding = PaddingValues(0.dp)) {
                AppText("View Details", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AuditLogRow(log: AuditLogDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val dt = log.timestamp.toStdlibInstant().toLocalDateTime(TimeZone.currentSystemDefault())
        val dateStr = "${dt.month.name.take(3)} ${dt.day}, ${dt.year}"
        val timeStr = "${dt.hour.toString().padStart(2, '0')}:${dt.minute.toString().padStart(2, '0')}:${dt.second.toString().padStart(2, '0')}"
        
        Column(Modifier.weight(1f)) {
            AppText(dateStr, style = MaterialTheme.typography.bodyMedium)
            AppText(timeStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        Row(modifier = Modifier.weight(1.5f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val initials = log.userName.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").take(2).uppercase()
            Box(
                modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AppText(initials, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Column {
                AppText(log.userName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                AppText(log.userRole, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        Column(Modifier.weight(1.5f)) {
            val isError = log.status == AuditStatus.CRITICAL || log.status == AuditStatus.FAILURE
            AppText(log.action, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = if (isError) AcademicError else Color.Unspecified)
            AppText(log.actionDetails ?: "", style = MaterialTheme.typography.labelSmall, color = if (isError) AcademicError.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        Box(Modifier.weight(0.8f)) {
            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(4.dp)) {
                AppText(log.module.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
        
        Row(modifier = Modifier.weight(0.8f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val statusColor = when(log.status) {
                AuditStatus.CRITICAL, AuditStatus.FAILURE -> AcademicError
                AuditStatus.WARNING -> AcademicWarning
                else -> MaterialTheme.colorScheme.primary
            }
            Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
            AppText(log.status.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall)
        }
        
        AppText(log.ipAddress ?: "-", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Box(modifier = Modifier.weight(0.8f), contentAlignment = Alignment.CenterEnd) {
            TextButton(onClick = {}, contentPadding = PaddingValues(0.dp)) {
                AppText("View Details", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
