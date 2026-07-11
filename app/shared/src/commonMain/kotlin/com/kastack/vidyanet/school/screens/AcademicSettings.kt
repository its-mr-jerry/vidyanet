package com.kastack.vidyanet.school.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kastack.vidyanet.commonUi.components.AppText
import com.kastack.vidyanet.school.components.AdaptiveIconButton
import com.kastack.vidyanet.school.components.HeaderAction
import com.kastack.vidyanet.school.components.SchoolSettingsHeader
import com.kastack.vidyanet.school.components.StatusBadge
import com.kastack.vidyanet.school.viewModels.AcademicSettingsViewModel
import com.kastack.vidyanet.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AcademicSettings(
    schoolId: String,
    viewModel: AcademicSettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(schoolId) {
        viewModel.loadSettings(schoolId)
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Academic settings saved successfully!")
            viewModel.resetSaveSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header
            SchoolSettingsHeader(
                title = "Academic Settings",
                subtitle = "Configure academic structure, grading systems, and school policies to match your institution's specific requirements.",
                breadcrumbs = listOf("Settings", "Academic Settings"),
                primaryAction = HeaderAction(
                    label = "Save Changes",
                    onClick = { viewModel.saveSettings() },
                    isLoading = uiState.isSaving
                ),
                secondaryAction = HeaderAction(
                    label = "Cancel",
                    onClick = { /* Handle cancel */ }
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Main Content: Adaptive Bento Grid Style
                BoxWithConstraints {
                    if (maxWidth > 1000.dp) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            // Left Column
                            Column(modifier = Modifier.weight(0.6f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                AcademicYearSection(uiState.academicSessions)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                    AttendanceRulesSection(Modifier.weight(1f))
                                    PromotionRulesSection(Modifier.weight(1f))
                                }
                            }
                            // Right Column
                            Column(modifier = Modifier.weight(0.4f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                GradingSystemSection()
                                HolidaysCalendarSection()
                            }
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            AcademicYearSection(uiState.academicSessions)
                            GradingSystemSection()
                            AttendanceRulesSection()
                            PromotionRulesSection()
                            HolidaysCalendarSection()
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun AcademicYearSection(sessions: List<com.kastack.vidyanet.school.viewModels.AcademicSession>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        BoxWithConstraints {
            val isCompact = maxWidth < 500.dp
            if (isCompact) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary)
                        AppText("Academic Year", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    AdaptiveIconButton(
                        label = "Add New Session",
                        icon = Icons.Default.Add,
                        onClick = { },
                        isMobile = isCompact
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary)
                        AppText("Academic Year Management", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { }) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        AppText("Add New Session", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        sessions.forEach { session ->
            AcademicSessionCard(session)
        }
    }
}

@Composable
private fun AcademicSessionCard(session: com.kastack.vidyanet.school.viewModels.AcademicSession) {
    val isCurrent = session.status == "Current"
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        BoxWithConstraints(modifier = Modifier.padding(16.dp)) {
            val isCompact = maxWidth < 500.dp
            
            if (isCompact) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SessionIcon(session.status, isCurrent)
                        Column(modifier = Modifier.weight(1f)) {
                            AppText(session.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            AppText(session.duration, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.MoreVert, null, tint = MaterialTheme.colorScheme.outline)
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SessionStatusBadge(session.status)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(1f)) {
                        SessionIcon(session.status, isCurrent)
                        Column {
                            AppText(session.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            AppText(session.duration, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SessionStatusBadge(session.status)
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.MoreVert, null, tint = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionIcon(status: String, isCurrent: Boolean) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                if (isCurrent) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) 
                else MaterialTheme.colorScheme.surfaceContainer, 
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = when(status) {
                "Current" -> Icons.Default.EventAvailable
                "Upcoming" -> Icons.Default.Update
                else -> Icons.Default.History
            },
            contentDescription = null,
            tint = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun SessionStatusBadge(status: String) {
    val badgeColor = when(status) {
        "Current" -> AcademicSuccess
        "Upcoming" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }
    StatusBadge(text = status, color = badgeColor)
}

@Composable
private fun GradingSystemSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Default.Grade, null, tint = MaterialTheme.colorScheme.primary)
            AppText("Grading System", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surfaceContainerLowest
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppText("Primary Grading Scale", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        GradingScaleButton("Letter", "A, B, C...", true, Modifier.weight(1f))
                        GradingScaleButton("GPA", "4.0 Scale", false, Modifier.weight(1f))
                        GradingScaleButton("Percent", "0 - 100%", false, Modifier.weight(1f))
                    }
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SettingsInputField("Pass Marks (%)", "35", Modifier.weight(1f))
                    SettingsDropdownField("GPA Decimals", "2 Places", Modifier.weight(1f))
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Checkbox(checked = false, onCheckedChange = {})
                    AppText("Enable Weighted GPA Calculation", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun GradingScaleButton(label: String, subLabel: String, selected: Boolean, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent,
        onClick = {}
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppText(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            AppText(subLabel, style = MaterialTheme.typography.labelSmall, color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun AttendanceRulesSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.AutoMirrored.Filled.Rule, null, tint = MaterialTheme.colorScheme.primary)
            AppText("Attendance Rules", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        
        Surface(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surfaceContainerLowest
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AttendanceRuleRow("Daily Attendance", "Mark once per day", true)
                    AttendanceRuleRow("Subject-wise", "Per lecture session", false)
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppText("Late Threshold (Mins)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = "15",
                            onValueChange = {},
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.NotificationsActive, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                    }
                    AppText("Triggers SMS to parents after threshold.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
            }
        }
    }
}

@Composable
private fun AttendanceRuleRow(title: String, subtitle: String, active: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { }
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            AppText(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            AppText(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = active, onCheckedChange = {})
    }
}

@Composable
private fun HolidaysCalendarSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Default.BedtimeOff, null, tint = MaterialTheme.colorScheme.primary)
            AppText("Holidays & Calendar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surfaceContainerLowest
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText("October 2024", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.ChevronLeft, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
                        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
                    }
                }
                
                // Calendar Mock
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                            AppText(day, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    // Simplistic calendar grid mock
                    CalendarRow(listOf("29", "30", "1", "2", "3", "4", "5"), holidayIdx = 3)
                    CalendarRow(listOf("6", "7", "8", "9", "10", "11", "12"), holidayIdx = 5)
                }
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                    AppText("2nd Oct: National Holiday", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun CalendarRow(days: List<String>, holidayIdx: Int = -1) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceAround) {
        days.forEachIndexed { index, day ->
            val isHoliday = index == holidayIdx
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(if (isHoliday) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                AppText(
                    text = day, 
                    style = MaterialTheme.typography.bodySmall, 
                    fontWeight = if (isHoliday) FontWeight.Bold else FontWeight.Normal,
                    color = if (isHoliday) MaterialTheme.colorScheme.primary else if (day.length > 1 && day.startsWith("2") && day != "2") MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun PromotionRulesSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = MaterialTheme.colorScheme.primary)
            AppText("Promotion Rules", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        
        Surface(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surfaceContainerLowest
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        AppText("Automatic promotion occurs at the end of the current session based on criteria below.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppText("Min. Overall Percentage", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Slider(
                        value = 0.4f, 
                        onValueChange = {},
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        AppText("0%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        AppText("40% Minimum", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        AppText("100%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Checkbox(checked = true, onCheckedChange = {})
                        AppText("Require No Dues Clearance", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Checkbox(checked = true, onCheckedChange = {})
                        AppText("Min. Attendance (75%)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsInputField(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        AppText(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF8FAFC),
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SettingsDropdownField(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        AppText(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
            readOnly = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF8FAFC),
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}
