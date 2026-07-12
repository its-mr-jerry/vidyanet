package com.kastack.vidyanet.school.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kastack.vidyanet.commonUi.components.*
import com.kastack.vidyanet.constants.IndiaConstants
import com.kastack.vidyanet.models.schoolUser.UpdateSchoolSettingsRequest
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import com.kastack.vidyanet.school.components.AdaptiveIconButton
import com.kastack.vidyanet.school.components.HeaderAction
import com.kastack.vidyanet.school.components.SchoolSettingsHeader
import com.kastack.vidyanet.school.components.StatusBadge
import com.kastack.vidyanet.school.viewModels.SchoolSettingsViewModel
import com.kastack.vidyanet.theme.AcademicError
import com.kastack.vidyanet.theme.AcademicSuccess
import com.kastack.vidyanet.validators.ValidationSchema
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun SchoolSettings(
    schoolId: String,
    viewModel: SchoolSettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Form State
    var schoolName by remember(uiState.school) { mutableStateOf(uiState.school?.schoolName ?: "") }
    var regNo by remember(uiState.settings) { mutableStateOf(uiState.settings?.registrationNumber ?: "") }
    var email by remember(uiState.school) { mutableStateOf(uiState.school?.email ?: "") }
    var phone by remember(uiState.school) { mutableStateOf(uiState.school?.phone ?: "") }
    var website by remember(uiState.school) { mutableStateOf(uiState.school?.website ?: "") }
    var address by remember(uiState.school) { mutableStateOf(uiState.school?.address ?: "") }
    var motto by remember(uiState.settings) { mutableStateOf(uiState.settings?.motto ?: "") }
    var board by remember(uiState.settings) { mutableStateOf(uiState.settings?.affiliationBoard ?: "") }
    var estDate by remember(uiState.settings) { mutableStateOf(uiState.settings?.establishmentDate ?: "") }
    var brandColor by remember(uiState.settings) { mutableStateOf(uiState.settings?.primaryBrandColor ?: "#4F46E5") }
    var logoUrl by remember(uiState.settings) { mutableStateOf(uiState.settings?.logoUrl) }
    var selectedLogoBytes by remember { mutableStateOf<ByteArray?>(null) }
    var maintenanceMode by remember(uiState.settings) { mutableStateOf(uiState.settings?.isMaintenanceMode ?: false) }
    var workingHours by remember(uiState.settings) { mutableStateOf(uiState.settings?.workingHours ?: emptyList()) }
    var branches by remember(uiState.settings) { mutableStateOf(uiState.settings?.branches ?: emptyList()) }

    var regNoError by remember { mutableStateOf<String?>(null) }
    var mottoError by remember { mutableStateOf<String?>(null) }
    var boardError by remember { mutableStateOf<String?>(null) }
    var estDateError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val imagePicker = rememberImagePicker(
        maxSizeMB = 1,
        onImageSelected = { selectedLogoBytes = it },
        onSizeExceeded = {
            scope.launch {
                snackbarHostState.showSnackbar("Logo size cannot exceed 1MB")
            }
        }
    )

    // Branch Editing State
    var branchToEdit by remember { mutableStateOf<Pair<Int, com.kastack.vidyanet.models.schoolUser.SchoolBranchDto>?>(null) }
    var showBranchDialog by remember { mutableStateOf(false) }

    LaunchedEffect(schoolId) {
        viewModel.loadSettings(schoolId)
    }

    AppDialog(state = uiState.dialogState, onDismissRequest = { viewModel.hideDialog() })

    if (showBranchDialog) {
        BranchDialog(
            branch = branchToEdit?.second,
            onDismiss = { 
                showBranchDialog = false
                branchToEdit = null
            },
            onSave = { updatedBranch ->
                val newBranches = branches.toMutableList()
                if (branchToEdit != null) {
                    newBranches[branchToEdit!!.first] = updatedBranch
                } else {
                    newBranches.add(updatedBranch)
                }
                branches = newBranches
                showBranchDialog = false
                branchToEdit = null
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Sticky Header Actions
            SchoolSettingsHeader(
                title = "School Settings",
                subtitle = "Manage your institution's profile, branding, and operational parameters.",
                breadcrumbs = listOf("Settings", "School Settings"),
                primaryAction = if (uiState.canEdit) HeaderAction(
                    label = "Save Changes",
                    icon = Icons.Default.Save,
                    onClick = { 
                        var hasError = false
                        try { ValidationSchema.school.regNo.validate(regNo) } catch (e: Exception) { regNoError = e.message; hasError = true }
                        try { ValidationSchema.school.motto.validate(motto) } catch (e: Exception) { mottoError = e.message; hasError = true }
                        try { ValidationSchema.school.affiliationBoard.validate(board) } catch (e: Exception) { boardError = e.message; hasError = true }
                        try { ValidationSchema.school.establishmentDate.validate(estDate) } catch (e: Exception) { estDateError = e.message; hasError = true }

                        if (!hasError) {
                            viewModel.saveSettings(
                                schoolId,
                                UpdateSchoolSettingsRequest(
                                    registrationNumber = regNo,
                                    motto = motto,
                                    establishmentDate = estDate,
                                    affiliationBoard = board,
                                    primaryBrandColor = brandColor,
                                    logoBase64 = selectedLogoBytes?.let { Base64.encode(it) },
                                    isMaintenanceMode = maintenanceMode,
                                    workingHours = workingHours,
                                    branches = branches
                                )
                            ) 
                        }
                    },
                    isLoading = uiState.isSaving
                ) else null,
                secondaryAction = if (uiState.canEdit) HeaderAction(
                    label = "Discard",
                    onClick = { 
                        viewModel.loadSettings(schoolId)
                        selectedLogoBytes = null
                    }
                ) else null
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // Layout: Adaptive Bento Grid Style
                BoxWithConstraints {
                    val readOnly = !uiState.canEdit
                    if (maxWidth > 1000.dp) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            Column(modifier = Modifier.weight(0.65f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                SchoolProfileSection(
                                    schoolName = schoolName,
                                    regNo = regNo,
                                    onRegNoChange = { regNo = it; regNoError = null },
                                    regNoError = regNoError,
                                    motto = motto,
                                    onMottoChange = { motto = it; mottoError = null },
                                    mottoError = mottoError,
                                    board = board,
                                    onBoardChange = { board = it; boardError = null },
                                    boardError = boardError,
                                    estDate = estDate,
                                    onEstDateChange = { estDate = it; estDateError = null },
                                    estDateError = estDateError,
                                    readOnly = readOnly
                                )
                                ContactInfoSection(
                                    email = email,
                                    phone = phone,
                                    website = website,
                                    address = address
                                )
                                BranchesSection(
                                    branches = branches,
                                    onAddBranch = { 
                                        branchToEdit = null
                                        showBranchDialog = true
                                    },
                                    onEditBranch = { index, branch ->
                                        branchToEdit = index to branch
                                        showBranchDialog = true
                                    },
                                    onDeleteBranch = { index ->
                                        branches = branches.toMutableList().apply { removeAt(index) }
                                    },
                                    readOnly = readOnly
                                )
                            }
                            Column(modifier = Modifier.weight(0.35f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                BrandingSection(
                                    brandColor = brandColor,
                                    logoUrl = logoUrl,
                                    selectedLogoBytes = selectedLogoBytes,
                                    onPickLogo = imagePicker,
                                    readOnly = readOnly
                                )
                                WorkingHoursSection(
                                    hours = workingHours,
                                    onHoursChange = { workingHours = it },
                                    readOnly = readOnly
                                )
                                MaintenanceModeSection(maintenanceMode, onToggle = { maintenanceMode = it }, readOnly = readOnly)
                            }
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            SchoolProfileSection(
                                schoolName = schoolName,
                                regNo = regNo,
                                onRegNoChange = { regNo = it; regNoError = null },
                                regNoError = regNoError,
                                motto = motto,
                                onMottoChange = { motto = it; mottoError = null },
                                mottoError = mottoError,
                                board = board,
                                onBoardChange = { board = it; boardError = null },
                                boardError = boardError,
                                estDate = estDate,
                                onEstDateChange = { estDate = it; estDateError = null },
                                estDateError = estDateError,
                                readOnly = readOnly
                            )
                            BrandingSection(
                                brandColor = brandColor,
                                logoUrl = logoUrl,
                                selectedLogoBytes = selectedLogoBytes,
                                onPickLogo = imagePicker,
                                readOnly = readOnly
                            )
                            ContactInfoSection(
                                email = email,
                                phone = phone,
                                website = website,
                                address = address
                            )
                            WorkingHoursSection(
                                hours = workingHours,
                                onHoursChange = { workingHours = it },
                                readOnly = readOnly
                            )
                            BranchesSection(
                                branches = branches,
                                onAddBranch = { 
                                    branchToEdit = null
                                    showBranchDialog = true
                                },
                                onEditBranch = { index, branch ->
                                    branchToEdit = index to branch
                                    showBranchDialog = true
                                },
                                onDeleteBranch = { index ->
                                    branches = branches.toMutableList().apply { removeAt(index) }
                                },
                                readOnly = readOnly
                            )
                            MaintenanceModeSection(maintenanceMode, onToggle = { maintenanceMode = it }, readOnly = readOnly)
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    iconContainerColor: Color,
    iconContentColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconContainerColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = iconContentColor, modifier = Modifier.size(20.dp))
                }
                AppText(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            content()
        }
    }
}

@Composable
private fun SchoolProfileSection(
    schoolName: String,
    regNo: String,
    onRegNoChange: (String) -> Unit,
    regNoError: String?,
    motto: String,
    onMottoChange: (String) -> Unit,
    mottoError: String?,
    board: String,
    onBoardChange: (String) -> Unit,
    boardError: String?,
    estDate: String,
    onEstDateChange: (String) -> Unit,
    estDateError: String?,
    readOnly: Boolean
) {
    SettingsCard(
        title = "School Profile",
        icon = Icons.AutoMirrored.Filled.Assignment,
        iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
        iconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AppTextField(ValidationSchema.school.name, schoolName, {}, Modifier.weight(1f), readOnly = true)
                AppTextField(ValidationSchema.school.regNo, regNo, onRegNoChange, Modifier.weight(1f), readOnly = readOnly, error = regNoError)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AppTextField(ValidationSchema.school.affiliationBoard, board, onBoardChange, Modifier.weight(1f), readOnly = readOnly, error = boardError)
                AppTextField(ValidationSchema.school.establishmentDate, estDate, onEstDateChange, Modifier.weight(1f), readOnly = readOnly, error = estDateError)
            }
            AppTextArea(ValidationSchema.school.motto, motto, onMottoChange, readOnly = readOnly, error = mottoError)
        }
    }
}

@Composable
private fun BrandingSection(
    brandColor: String,
    logoUrl: String?,
    selectedLogoBytes: ByteArray?,
    onPickLogo: () -> Unit,
    readOnly: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        SettingsCard(
            title = "Branding",
            icon = Icons.Default.Palette,
            iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            iconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        .then(if (!readOnly) Modifier.clickable { onPickLogo() } else Modifier)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedLogoBytes != null) {
                            coil3.compose.AsyncImage(
                                model = selectedLogoBytes,
                                contentDescription = "Selected Logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else if (!logoUrl.isNullOrBlank()) {
                            coil3.compose.AsyncImage(
                                model = logoUrl,
                                contentDescription = "Current Logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.School, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    if (!readOnly) {
                        AppText(
                            text = if (selectedLogoBytes != null || !logoUrl.isNullOrBlank()) "Change Logo" else "Upload Square Logo",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        AppText("SVG, PNG up to 1MB", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }

                Column {
                    AppText("Primary Brand Color", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val color = try { Color(brandColor.removePrefix("#").toLong(16) or 0xFF000000) } catch (_: Exception) { Color(0xFF4F46E5) }
                            Box(modifier = Modifier.size(32.dp).background(color, RoundedCornerShape(6.dp)))
                            AppText(brandColor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactInfoSection(
    email: String,
    phone: String,
    website: String,
    address: String
) {
    SettingsCard(
        title = "Contact Information",
        icon = Icons.Default.AlternateEmail,
        iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
        iconContentColor = MaterialTheme.colorScheme.onTertiaryContainer
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AppTextField(ValidationSchema.school.email, email, {}, Modifier.weight(1f), readOnly = true)
                AppTextField(ValidationSchema.school.phone, phone, {}, Modifier.weight(1f), readOnly = true)
            }
            AppTextField(com.kastack.vidyanet.validators.FieldSchema("Website"), website, {}, readOnly = true)
            AppTextArea(ValidationSchema.school.address, address, {}, rows = 2, readOnly = true)
        }
    }
}

@Composable
private fun WorkingHoursSection(
    hours: List<com.kastack.vidyanet.models.schoolUser.WorkingHourDto>,
    onHoursChange: (List<com.kastack.vidyanet.models.schoolUser.WorkingHourDto>) -> Unit,
    readOnly: Boolean
) {
    SettingsCard(
        title = "Working Hours",
        icon = Icons.Default.Schedule,
        iconContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        iconContentColor = MaterialTheme.colorScheme.primary
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val days = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
            days.forEach { day ->
                val hour = hours.find { it.dayOfWeek == day }
                WorkingDayRow(
                    day = day,
                    start = hour?.openingTime ?: "08:00",
                    end = hour?.closingTime ?: "16:00",
                    isOpen = hour?.isClosed?.not() ?: (day != "SUNDAY"),
                    onToggle = { isOpen ->
                        val newHours = hours.toMutableList()
                        val index = newHours.indexOfFirst { it.dayOfWeek == day }
                        val updated = (hour ?: com.kastack.vidyanet.models.schoolUser.WorkingHourDto(dayOfWeek = day, openingTime = "08:00", closingTime = "16:00", isClosed = !isOpen))
                            .copy(isClosed = !isOpen)
                        if (index != -1) newHours[index] = updated else newHours.add(updated)
                        onHoursChange(newHours)
                    },
                    onTimeChange = { start, end ->
                        val newHours = hours.toMutableList()
                        val index = newHours.indexOfFirst { it.dayOfWeek == day }
                        val updated = (hour ?: com.kastack.vidyanet.models.schoolUser.WorkingHourDto(dayOfWeek = day, openingTime = start, closingTime = end, isClosed = false))
                            .copy(openingTime = start, closingTime = end)
                        if (index != -1) newHours[index] = updated else newHours.add(updated)
                        onHoursChange(newHours)
                    },
                    readOnly = readOnly
                )
            }
        }
    }
}

@Composable
private fun WorkingDayRow(
    day: String,
    start: String,
    end: String,
    isOpen: Boolean,
    onToggle: (Boolean) -> Unit,
    onTimeChange: (String, String) -> Unit,
    readOnly: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isOpen) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant),
        color = if (isOpen) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
    ) {
        BoxWithConstraints {
            val isCompact = maxWidth < 400.dp
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(if (isCompact) 4.dp else 12.dp)) {
                    Checkbox(checked = isOpen, onCheckedChange = onToggle, enabled = !readOnly)
                    AppText(day.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.widthIn(min = if (isCompact) 60.dp else 80.dp))
                }
                if (isOpen) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        var showStartPicker by remember { mutableStateOf(false) }
                        var showEndPicker by remember { mutableStateOf(false) }

                        val startTime = start.split(":")
                        val startHour = startTime.getOrNull(0)?.toIntOrNull() ?: 8
                        val startMin = startTime.getOrNull(1)?.toIntOrNull() ?: 0

                        val endTime = end.split(":")
                        val endHour = endTime.getOrNull(0)?.toIntOrNull() ?: 16
                        val endMin = endTime.getOrNull(1)?.toIntOrNull() ?: 0

                        BasicTimeField(start, enabled = !readOnly) { if (!readOnly) showStartPicker = true }
                        AppText("—", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outlineVariant)
                        BasicTimeField(end, enabled = !readOnly) { if (!readOnly) showEndPicker = true }

                        if (showStartPicker) {
                            AppTimePickerDialog(
                                onDismissRequest = { showStartPicker = false },
                                onConfirm = { h, m ->
                                    onTimeChange("${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}", end)
                                    showStartPicker = false
                                },
                                initialHour = startHour,
                                initialMinute = startMin,
                                title = "Opening Time"
                            )
                        }

                        if (showEndPicker) {
                            AppTimePickerDialog(
                                onDismissRequest = { showEndPicker = false },
                                onConfirm = { h, m ->
                                    onTimeChange(start, "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}")
                                    showEndPicker = false
                                },
                                initialHour = endHour,
                                initialMinute = endMin,
                                title = "Closing Time"
                            )
                        }
                    }
                } else {
                    AppText("CLOSED", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun BasicTimeField(value: String, enabled: Boolean = true, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = if (enabled) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            AppText(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun BranchesSection(
    branches: List<com.kastack.vidyanet.models.schoolUser.SchoolBranchDto>,
    onAddBranch: () -> Unit,
    onEditBranch: (Int, com.kastack.vidyanet.models.schoolUser.SchoolBranchDto) -> Unit,
    onDeleteBranch: (Int) -> Unit,
    readOnly: Boolean
) {
    SettingsCard(
        title = "School Branches",
        icon = Icons.Default.Hub,
        iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
        iconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Column {
            BoxWithConstraints {
                val isCompact = maxWidth < 500.dp
                if (isCompact) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppText("Manage branches", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                        if (!readOnly) {
                            AdaptiveIconButton(
                                label = "Add Branch",
                                icon = Icons.Default.AddLocationAlt,
                                onClick = onAddBranch,
                                isMobile = true
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            AppText("Manage physical campuses and administrative units", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (!readOnly) {
                            AdaptiveIconButton(
                                label = "Add Branch",
                                icon = Icons.Default.AddLocationAlt,
                                onClick = onAddBranch,
                                isMobile = false
                            )
                        }
                    }
                }
            }

            // Simple Table-like structure
            Box(modifier = Modifier.fillMaxWidth()) {
                BoxWithConstraints {
                    val isMobile = maxWidth < 600.dp
                    if (isMobile) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            branches.forEachIndexed { index, branch ->
                                BranchMobileCard(
                                    name = branch.name, 
                                    tag = branch.type, 
                                    location = "${branch.city}, ${branch.state}", 
                                    contact = branch.contactPerson, 
                                    isActive = branch.status == "ACTIVE",
                                    onEdit = { onEditBranch(index, branch) },
                                    onDelete = { onDeleteBranch(index) },
                                    readOnly = readOnly
                                )
                            }
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLow).padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppText("Branch Name", Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                AppText("Location", Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                AppText("Contact Person", Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                AppText("Status", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                AppText("Actions", Modifier.weight(0.8f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            
                            branches.forEachIndexed { index, branch ->
                                BranchRow(
                                    name = branch.name, 
                                    tag = branch.type, 
                                    location = "${branch.city}, ${branch.state}", 
                                    contact = branch.contactPerson, 
                                    isActive = branch.status == "ACTIVE",
                                    onEdit = { onEditBranch(index, branch) },
                                    onDelete = { onDeleteBranch(index) },
                                    readOnly = readOnly
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BranchMobileCard(
    name: String, 
    tag: String, 
    location: String, 
    contact: String, 
    isActive: Boolean, 
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    readOnly: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    AppText(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    AppText(tag, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, letterSpacing = 1.sp)
                }
                val statusColor = if (isActive) AcademicSuccess else AcademicError
                Surface(color = statusColor.copy(alpha = 0.1f), shape = CircleShape) {
                    AppText(if (isActive) "Active" else "Inactive", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = statusColor)
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                    AppText(location, style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                    AppText(contact, style = MaterialTheme.typography.bodySmall)
                }
            }
            
            if (!readOnly) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(20.dp), tint = AcademicError) }
                }
            }
        }
    }
}

@Composable
private fun BranchRow(
    name: String, 
    tag: String, 
    location: String, 
    contact: String, 
    isActive: Boolean, 
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    readOnly: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1.5f)) {
            AppText(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            AppText(tag, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, letterSpacing = 1.sp)
        }
        AppText(location, Modifier.weight(1.5f), style = MaterialTheme.typography.bodySmall)
        AppText(contact, Modifier.weight(1.5f),style = MaterialTheme.typography.bodySmall)

        Box(Modifier.weight(1f)) {
            val statusColor = if (isActive) AcademicSuccess else AcademicError
            StatusBadge(text = if (isActive) "Active" else "Inactive", color = statusColor)
        }
        Row(Modifier.weight(0.8f), horizontalArrangement = Arrangement.End) {
            if (!readOnly) {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.outline) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp), tint = AcademicError) }
            }
        }
    }
}

@Composable
private fun BranchDialog(
    branch: com.kastack.vidyanet.models.schoolUser.SchoolBranchDto?,
    onDismiss: () -> Unit,
    onSave: (com.kastack.vidyanet.models.schoolUser.SchoolBranchDto) -> Unit
) {
    var name by remember { mutableStateOf(branch?.name ?: "") }
    var type by remember { mutableStateOf(branch?.type ?: "ACADEMIC") }
    var address by remember { mutableStateOf(branch?.address ?: "") }
    var city by remember { mutableStateOf(branch?.city ?: "") }
    var state by remember { mutableStateOf(branch?.state ?: "") }
    var postalCode by remember { mutableStateOf(branch?.postalCode ?: "") }
    var contactPerson by remember { mutableStateOf(branch?.contactPerson ?: "") }
    var phone by remember { mutableStateOf(branch?.phone ?: "") }
    var email by remember { mutableStateOf(branch?.email ?: "") }
    var status by remember { mutableStateOf(branch?.status ?: "ACTIVE") }

    var validationError by remember { mutableStateOf<String?>(null) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.widthIn(max = 500.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AppText(if (branch == null) "Add New Branch" else "Edit Branch", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                
                if (validationError != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AppText(
                            text = validationError!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                AppTextField(ValidationSchema.branch.name, name, { 
                    name = it
                    validationError = null 
                })
                AppTextField(ValidationSchema.branch.type, type, { 
                    type = it
                    validationError = null
                })
                AppTextArea(ValidationSchema.branch.address, address, { 
                    address = it
                    validationError = null
                }, rows = 2)
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppTextField(ValidationSchema.branch.city, city, { 
                        city = it
                        validationError = null
                    }, Modifier.weight(1f))
                    AppFormDropdown("State", state, IndiaConstants.states, { 
                        state = it
                        validationError = null
                    }, Modifier.weight(1f))
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppTextField(ValidationSchema.branch.postalCode, postalCode, { 
                        postalCode = it
                        validationError = null
                    }, Modifier.weight(1f))
                    AppTextField(ValidationSchema.branch.contactPerson, contactPerson, { 
                        contactPerson = it
                        validationError = null
                    }, Modifier.weight(1f))
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppTextField(ValidationSchema.branch.phone, phone, { 
                        phone = it
                        validationError = null
                    }, Modifier.weight(1f))
                    AppTextField(ValidationSchema.branch.email, email, { 
                        email = it
                        validationError = null
                    }, Modifier.weight(1f))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = status == "ACTIVE", onCheckedChange = { status = if (it) "ACTIVE" else "INACTIVE" })
                    AppText("Active Status", style = MaterialTheme.typography.bodyMedium)
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDismiss) { AppText("Cancel") }
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = { 
                            val updatedBranch = com.kastack.vidyanet.models.schoolUser.SchoolBranchDto(
                                id = branch?.id,
                                name = name,
                                type = type,
                                address = address,
                                city = city,
                                state = state,
                                country = branch?.country ?: "India",
                                postalCode = postalCode,
                                contactPerson = contactPerson,
                                phone = phone,
                                email = email.takeIf { it.isNotBlank() },
                                status = status
                            )
                            try {
                                com.kastack.vidyanet.validators.SchoolSettingsValidator.validateBranch(updatedBranch)
                                onSave(updatedBranch)
                            } catch (e: com.kastack.vidyanet.validators.ValidationException) {
                                validationError = e.message
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        AppText("Save Branch")
                    }
                }
            }
        }
    }
}

@Composable
private fun MaintenanceModeSection(enabled: Boolean, onToggle: (Boolean) -> Unit, readOnly: Boolean) {
    SettingsCard(
        title = "Maintenance Mode",
        icon = Icons.Default.Build,
        iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
        iconContentColor = MaterialTheme.colorScheme.onTertiaryContainer
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AppText("Maintenance Mode", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                AppText("Restricts access to the portal while performing system updates or maintenance.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = enabled, onCheckedChange = onToggle, enabled = !readOnly)
        }
    }
}
