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
import com.kastack.vidyanet.commonUi.components.AppDialog
import com.kastack.vidyanet.commonUi.components.AppDialogState
import com.kastack.vidyanet.commonUi.components.AppDialogType
import com.kastack.vidyanet.commonUi.components.AppText
import com.kastack.vidyanet.models.schoolUser.AcademicSessionDto
import com.kastack.vidyanet.models.schoolUser.HolidayDto
import com.kastack.vidyanet.models.schoolUser.UpdateAcademicSettingsRequest
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

    // Form State
    var gradingScale by remember(uiState.settings) { mutableStateOf(uiState.settings?.gradingScale ?: "LETTER") }
    var passMarks by remember(uiState.settings) { mutableStateOf(uiState.settings?.passMarks?.toString() ?: "35") }
    var gpaDecimals by remember(uiState.settings) { mutableStateOf(uiState.settings?.gpaDecimals?.toString() ?: "2") }
    var isWeightedGpa by remember(uiState.settings) { mutableStateOf(uiState.settings?.isWeightedGpa ?: false) }
    var attendanceMode by remember(uiState.settings) { mutableStateOf(uiState.settings?.attendanceMode ?: "DAILY") }
    var lateThreshold by remember(uiState.settings) { mutableStateOf(uiState.settings?.lateThresholdMinutes?.toString() ?: "15") }
    var minPromotionPercentage by remember(uiState.settings) { mutableStateOf(uiState.settings?.minPromotionPercentage?.toFloat() ?: 40f) }
    var minPromotionAttendance by remember(uiState.settings) { mutableStateOf(uiState.settings?.minPromotionAttendance?.toString() ?: "75") }
    var requireNoDues by remember(uiState.settings) { mutableStateOf(uiState.settings?.requireNoDues ?: true) }

    var showAddSessionDialog by remember { mutableStateOf(false) }
    var showAddHolidayDialog by remember { mutableStateOf(false) }

    LaunchedEffect(schoolId) {
        viewModel.loadSettings(schoolId)
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Academic settings saved successfully!")
            viewModel.resetSaveSuccess()
        }
    }

    if (uiState.error != null) {
        AppDialog(
            state = AppDialogState(
                isVisible = true,
                type = AppDialogType.ERROR,
                title = "Error",
                message = uiState.error ?: "An unexpected error occurred",
                confirmLabel = "OK",
                onConfirm = { viewModel.clearError() }
            ),
            onDismissRequest = { viewModel.clearError() }
        )
    }

    if (showAddSessionDialog) {
        AddSessionDialog(
            onDismiss = { showAddSessionDialog = false },
            onSave = { name, start, end ->
                viewModel.addSession(schoolId, name, start, end)
                showAddSessionDialog = false
            }
        )
    }

    if (showAddHolidayDialog) {
        AddHolidayDialog(
            onDismiss = { showAddHolidayDialog = false },
            onSave = { name, date ->
                viewModel.addHoliday(schoolId, name, date)
                showAddHolidayDialog = false
            }
        )
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
                    onClick = { 
                        viewModel.saveSettings(
                            schoolId,
                            UpdateAcademicSettingsRequest(
                                gradingScale = gradingScale,
                                passMarks = passMarks.toIntOrNull(),
                                gpaDecimals = gpaDecimals.toIntOrNull(),
                                isWeightedGpa = isWeightedGpa,
                                attendanceMode = attendanceMode,
                                lateThresholdMinutes = lateThreshold.toIntOrNull(),
                                minPromotionPercentage = minPromotionPercentage.toInt(),
                                minPromotionAttendance = minPromotionAttendance.toIntOrNull(),
                                requireNoDues = requireNoDues
                            )
                        ) 
                    },
                    isLoading = uiState.isSaving
                ),
                secondaryAction = HeaderAction(
                    label = "Cancel",
                    onClick = { viewModel.loadSettings(schoolId) }
                )
            )

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
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
                                    AcademicYearSection(
                                        sessions = uiState.settings?.academicSessions ?: emptyList(),
                                        onAddSession = { showAddSessionDialog = true },
                                        onDeleteSession = { viewModel.deleteSession(schoolId, it) }
                                    )
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                        AttendanceRulesSection(
                                            attendanceMode = attendanceMode,
                                            onAttendanceModeChange = { attendanceMode = it },
                                            lateThreshold = lateThreshold,
                                            onLateThresholdChange = { lateThreshold = it },
                                            modifier = Modifier.weight(1f)
                                        )
                                        PromotionRulesSection(
                                            minPercentage = minPromotionPercentage,
                                            onMinPercentageChange = { minPromotionPercentage = it },
                                            requireNoDues = requireNoDues,
                                            onRequireNoDuesChange = { requireNoDues = it },
                                            minAttendance = minPromotionAttendance,
                                            onMinAttendanceChange = { minPromotionAttendance = it },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                // Right Column
                                Column(modifier = Modifier.weight(0.4f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                    GradingSystemSection(
                                        gradingScale = gradingScale,
                                        onGradingScaleChange = { gradingScale = it },
                                        passMarks = passMarks,
                                        onPassMarksChange = { passMarks = it },
                                        gpaDecimals = gpaDecimals,
                                        onGpaDecimalsChange = { gpaDecimals = it },
                                        isWeightedGpa = isWeightedGpa,
                                        onIsWeightedGpaChange = { isWeightedGpa = it }
                                    )
                                    HolidaysCalendarSection(
                                        holidays = uiState.settings?.holidays ?: emptyList(),
                                        onAddHoliday = { showAddHolidayDialog = true },
                                        onDeleteHoliday = { viewModel.deleteHoliday(schoolId, it) }
                                    )
                                }
                            }
                        } else {
                            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                AcademicYearSection(
                                    sessions = uiState.settings?.academicSessions ?: emptyList(),
                                    onAddSession = { showAddSessionDialog = true },
                                    onDeleteSession = { viewModel.deleteSession(schoolId, it) }
                                )
                                GradingSystemSection(
                                    gradingScale = gradingScale,
                                    onGradingScaleChange = { gradingScale = it },
                                    passMarks = passMarks,
                                    onPassMarksChange = { passMarks = it },
                                    gpaDecimals = gpaDecimals,
                                    onGpaDecimalsChange = { gpaDecimals = it },
                                    isWeightedGpa = isWeightedGpa,
                                    onIsWeightedGpaChange = { isWeightedGpa = it }
                                )
                                AttendanceRulesSection(
                                    attendanceMode = attendanceMode,
                                    onAttendanceModeChange = { attendanceMode = it },
                                    lateThreshold = lateThreshold,
                                    onLateThresholdChange = { lateThreshold = it }
                                )
                                PromotionRulesSection(
                                    minPercentage = minPromotionPercentage,
                                    onMinPercentageChange = { minPromotionPercentage = it },
                                    requireNoDues = requireNoDues,
                                    onRequireNoDuesChange = { requireNoDues = it },
                                    minAttendance = minPromotionAttendance,
                                    onMinAttendanceChange = { minPromotionAttendance = it }
                                )
                                HolidaysCalendarSection(
                                    holidays = uiState.settings?.holidays ?: emptyList(),
                                    onAddHoliday = { showAddHolidayDialog = true },
                                    onDeleteHoliday = { viewModel.deleteHoliday(schoolId, it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
private fun AcademicYearSection(
    sessions: List<AcademicSessionDto>,
    onAddSession: () -> Unit,
    onDeleteSession: (Long) -> Unit
) {
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
                        onClick = onAddSession,
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
                    TextButton(onClick = onAddSession) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        AppText("Add New Session", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        sessions.forEach { session ->
            AcademicSessionCard(session, onDelete = { session.id?.let { onDeleteSession(it) } })
        }
        
        if (sessions.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                    AppText("No academic sessions defined.", color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun AcademicSessionCard(session: AcademicSessionDto, onDelete: () -> Unit) {
    val isCurrent = session.status == "CURRENT"
    
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
                            AppText("${session.startDate} — ${session.endDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, null, tint = AcademicError, modifier = Modifier.size(20.dp))
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
                            AppText("${session.startDate} — ${session.endDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SessionStatusBadge(session.status)
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, null, tint = AcademicError, modifier = Modifier.size(20.dp))
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
                "CURRENT" -> Icons.Default.EventAvailable
                "UPCOMING" -> Icons.Default.Update
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
        "CURRENT" -> AcademicSuccess
        "UPCOMING" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }
    StatusBadge(text = status, color = badgeColor)
}

@Composable
private fun GradingSystemSection(
    gradingScale: String,
    onGradingScaleChange: (String) -> Unit,
    passMarks: String,
    onPassMarksChange: (String) -> Unit,
    gpaDecimals: String,
    onGpaDecimalsChange: (String) -> Unit,
    isWeightedGpa: Boolean,
    onIsWeightedGpaChange: (Boolean) -> Unit
) {
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
                        GradingScaleButton("Letter", "A, B, C...", gradingScale == "LETTER", Modifier.weight(1f)) { onGradingScaleChange("LETTER") }
                        GradingScaleButton("GPA", "4.0 Scale", gradingScale == "GPA", Modifier.weight(1f)) { onGradingScaleChange("GPA") }
                        GradingScaleButton("Percent", "0 - 100%", gradingScale == "PERCENT", Modifier.weight(1f)) { onGradingScaleChange("PERCENT") }
                    }
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SettingsInputField("Pass Marks (%)", passMarks, onPassMarksChange, Modifier.weight(1f))
                    SettingsDropdownField("GPA Decimals", "$gpaDecimals Places", Modifier.weight(1f)) { onGpaDecimalsChange("2") }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Checkbox(checked = isWeightedGpa, onCheckedChange = onIsWeightedGpaChange)
                    AppText("Enable Weighted GPA Calculation", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun GradingScaleButton(label: String, subLabel: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent,
        onClick = onClick
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
private fun AttendanceRulesSection(
    attendanceMode: String,
    onAttendanceModeChange: (String) -> Unit,
    lateThreshold: String,
    onLateThresholdChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
                    AttendanceRuleRow("Daily Attendance", "Mark once per day", attendanceMode == "DAILY") { onAttendanceModeChange("DAILY") }
                    AttendanceRuleRow("Subject-wise", "Per lecture session", attendanceMode == "SUBJECT_WISE") { onAttendanceModeChange("SUBJECT_WISE") }
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppText("Late Threshold (Mins)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = lateThreshold,
                            onValueChange = onLateThresholdChange,
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
private fun AttendanceRuleRow(title: String, subtitle: String, active: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            AppText(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            AppText(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        RadioButton(selected = active, onClick = onClick)
    }
}

@Composable
private fun HolidaysCalendarSection(
    holidays: List<HolidayDto>,
    onAddHoliday: () -> Unit,
    onDeleteHoliday: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.BedtimeOff, null, tint = MaterialTheme.colorScheme.primary)
                AppText("Holidays & Calendar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = onAddHoliday) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                AppText("Add Holiday", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
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
                    AppText("Academic Year Holidays", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
                
                if (holidays.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        AppText("No holidays defined.", color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        holidays.forEach { holiday ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                                    Column {
                                        AppText(holiday.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        AppText(holiday.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                IconButton(onClick = { holiday.id?.let { onDeleteHoliday(it) } }) {
                                    Icon(Icons.Default.Delete, null, tint = AcademicError, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddHolidayDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, date: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { AppText("Add Holiday", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { AppText("Holiday Name") }, placeholder = { AppText("e.g. Diwali") })
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { AppText("Date") }, placeholder = { AppText("YYYY-MM-DD") })
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank() && date.isNotBlank()) onSave(name, date) }) {
                AppText("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                AppText("Cancel")
            }
        }
    )
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
private fun PromotionRulesSection(
    minPercentage: Float,
    onMinPercentageChange: (Float) -> Unit,
    requireNoDues: Boolean,
    onRequireNoDuesChange: (Boolean) -> Unit,
    minAttendance: String,
    onMinAttendanceChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
                        value = minPercentage / 100f, 
                        onValueChange = { onMinPercentageChange(it * 100f) },
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        AppText("0%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        AppText("${minPercentage.toInt()}% Minimum", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        AppText("100%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Checkbox(checked = requireNoDues, onCheckedChange = onRequireNoDuesChange)
                        AppText("Require No Dues Clearance", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val attendanceChecked = (minAttendance.toIntOrNull() ?: 0) > 0
                        Checkbox(checked = attendanceChecked, onCheckedChange = { if (it) onMinAttendanceChange("75") else onMinAttendanceChange("0") })
                        AppText("Min. Attendance ($minAttendance%)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsInputField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        AppText(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
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
private fun SettingsDropdownField(label: String, value: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        AppText(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
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

@Composable
private fun AddSessionDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, start: String, end: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { AppText("Add Academic Session", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { AppText("Session Name") }, placeholder = { AppText("e.g. 2024-25") })
                OutlinedTextField(value = start, onValueChange = { start = it }, label = { AppText("Start Date") }, placeholder = { AppText("YYYY-MM-DD") })
                OutlinedTextField(value = end, onValueChange = { end = it }, label = { AppText("End Date") }, placeholder = { AppText("YYYY-MM-DD") })
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onSave(name, start, end) }) {
                AppText("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                AppText("Cancel")
            }
        }
    )
}
