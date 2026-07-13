package com.kastack.vidyanet.superAdmin.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kastack.vidyanet.commonUi.components.AppText
import com.kastack.vidyanet.superAdmin.components.SuperAdminLayout
import com.kastack.vidyanet.superAdmin.viewModels.DashboardKpi
import com.kastack.vidyanet.superAdmin.viewModels.SystemHealthState
import com.kastack.vidyanet.superAdmin.viewModels.SuperAdminDashboardViewModel
import com.kastack.vidyanet.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SuperAdminDashboard(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: SuperAdminDashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SuperAdminLayout(
        currentDestination = "Dashboard",
        onNavigate = onNavigate,
        onLogout = {
            viewModel.logout()
            onLogout()
        },
        academicYear = uiState.academicYear
    ) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceContainerLow)) {
            DashboardContent(
                kpis = uiState.kpis,
                systemHealth = uiState.systemHealth
            )
        }
    }
}

@Composable
private fun DashboardContent(
    kpis: List<DashboardKpi>,
    systemHealth: SystemHealthState
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Welcome Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                AppText("Platform Overview", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                AppText("Real-time institutional analytics across all registered schools.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { AppText("Search globally...", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.width(360.dp).height(48.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }

        // KPI Grid
        KPIGrid(kpis)

        // System Health & Analytics Section
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                SystemHealthCard(systemHealth)
            }
            Box(modifier = Modifier.weight(1f)) {
                DistributionCard()
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Box(modifier = Modifier.weight(2f)) {
                EnrollmentTrendsCard()
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                RecentActivityCard()
                ExpiryAlertsCard()
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        AppText(
            text = "© 2023 VidyaNet Institutional Management Systems. All rights reserved.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SystemHealthCard(health: SystemHealthState) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            AppText("System Health", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            
            HealthMetricItem("CPU Usage", "${health.cpuUsage}%", Icons.Default.Memory, health.cpuUsage / 100f)
            HealthMetricItem("RAM Usage", health.ramUsed, Icons.Default.SettingsInputComponent, 0.62f) // Placeholder progress
            HealthMetricItem("Storage", health.storageUsed, Icons.Default.Storage, 0.24f)
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Dns, null, modifier = Modifier.size(16.dp), tint = AcademicSuccess)
                Spacer(modifier = Modifier.width(8.dp))
                AppText("Database Status:", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.weight(1f))
                AppText(health.dbStatus, style = MaterialTheme.typography.bodySmall, color = AcademicSuccess, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HealthMetricItem(label: String, value: String, icon: ImageVector, progress: Float) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            AppText(label, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.weight(1f))
            AppText(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = if (progress > 0.8f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
private fun KPIGrid(kpis: List<DashboardKpi>) {
    val rows = kpis.chunked(4)
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        rows.forEach { rowKpis ->
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                rowKpis.forEach { kpi ->
                    KPICard(
                        label = kpi.label,
                        value = kpi.value,
                        trend = kpi.trend,
                        trendIcon = if (kpi.trend.startsWith("+")) Icons.AutoMirrored.Filled.TrendingUp else if (kpi.trend.startsWith("-")) Icons.AutoMirrored.Filled.TrendingDown else null,
                        modifier = Modifier.weight(1f),
                        isError = kpi.isError
                    )
                }
                if (rowKpis.size < 4) {
                    repeat(4 - rowKpis.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun KPICard(
    label: String,
    value: String,
    trend: String,
    trendIcon: ImageVector?,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AppText(label.uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppText(
                        text = trend,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isError) MaterialTheme.colorScheme.error else Color(0xFF16A34A),
                        fontWeight = FontWeight.Bold
                    )
                    if (trendIcon != null) {
                        Icon(
                            imageVector = trendIcon,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = if (isError) MaterialTheme.colorScheme.error else Color(0xFF16A34A)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            AppText(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EnrollmentTrendsCard() {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                AppText("School register Trends", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                AppText(
                    text = "Last 12 Months",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth().height(280.dp).background(MaterialTheme.colorScheme.surfaceContainerLow)) {
                // Chart Placeholder
                AppText("Chart Placeholder", modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@Composable
private fun DistributionCard() {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            AppText("Board Distribution", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(32.dp))
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { 0.45f },
                    modifier = Modifier.size(160.dp),
                    strokeWidth = 16.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppText("124", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    AppText("Total Schools", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            DistributionItem("CBSE", "45%", MaterialTheme.colorScheme.primary)
            DistributionItem("ICSE", "25%", MaterialTheme.colorScheme.primaryContainer)
            DistributionItem("State Board", "30%", MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun DistributionItem(label: String, percent: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(8.dp))
            AppText(label, style = MaterialTheme.typography.bodySmall)
        }
        AppText(percent, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RecentActivityCard() {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AppText("RECENT ACTIVITIES", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            RecentActivityItem("New school registered", "Heritage Academy (HA-887)", "2 hours ago", MaterialTheme.colorScheme.primary)
            RecentActivityItem("Subscription renewed", "St. Marys Convent - Platinum", "5 hours ago", Color(0xFFF59E0B))
            RecentActivityItem("System Backup completed", "Auto-backup successful", "Yesterday, 11:45 PM", Color(0xFF16A34A), isLast = true)
        }
    }
}

@Composable
private fun RecentActivityItem(title: String, subtitle: String, time: String, color: Color, isLast: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
            if (!isLast) {
                Box(modifier = Modifier.width(1.dp).height(40.dp).background(MaterialTheme.colorScheme.outlineVariant))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            AppText(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            AppText(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            AppText(time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun ExpiryAlertsCard() {
    Surface(
        color = Color(0xFFFEF2F2),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFFEE2E2))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                AppText("EXPIRY ALERTS", style = MaterialTheme.typography.labelMedium, color = Color.Red, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFFEE2E2))
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        AppText("Little Flower School", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        AppText("Expires in 2 days", style = MaterialTheme.typography.labelSmall, color = Color.Red)
                    }
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        AppText("Remind", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
            }
        }
    }
}
