package com.kastack.vidyanet.school.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kastack.vidyanet.commonUi.components.AppText
import com.kastack.vidyanet.school.components.AdaptiveIconButton
import com.kastack.vidyanet.school.components.HeaderAction
import com.kastack.vidyanet.school.components.SchoolSettingsHeader
import com.kastack.vidyanet.school.components.StatusBadge
import com.kastack.vidyanet.school.viewModels.SchoolSettingsViewModel
import com.kastack.vidyanet.theme.AcademicError
import com.kastack.vidyanet.theme.AcademicSuccess
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SchoolSettings(
    schoolId: String,
    viewModel: SchoolSettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("School Profile", "Logo & Branding", "Contact Information", "Branches", "Working Hours")

    // Form State
    var schoolName by remember(uiState.school) { mutableStateOf(uiState.school?.schoolName ?: "EduCore International Academy") }
    var regNo by remember { mutableStateOf("SCH-2024-8849-DX") }
    var email by remember(uiState.school) { mutableStateOf(uiState.school?.email ?: "contact@educoreacademy.edu") }
    var phone by remember(uiState.school) { mutableStateOf(uiState.school?.phone ?: "+1 (555) 012-3456") }
    var website by remember(uiState.school) { mutableStateOf(uiState.school?.website ?: "www.educoreacademy.edu") }
    var address by remember(uiState.school) { mutableStateOf(uiState.school?.address ?: "123 Education Way, Silicon Valley, CA 94025, United States") }
    var motto by remember { mutableStateOf("Learning Today, Leading Tomorrow.") }

    LaunchedEffect(schoolId) {
        viewModel.loadSettings(schoolId)
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Settings saved successfully!")
            viewModel.resetSaveSuccess()
        }
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
                primaryAction = HeaderAction(
                    label = "Save Changes",
                    icon = Icons.Default.Save,
                    onClick = { viewModel.saveSettings() },
                    isLoading = uiState.isSaving
                ),
                secondaryAction = HeaderAction(
                    label = "Discard",
                    onClick = { viewModel.loadSettings(schoolId) }
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Section Tabs
                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) },
                    indicator = { 
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(selectedTab),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                AppText(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }

                // Layout: Adaptive Bento Grid Style
                BoxWithConstraints {
                    if (maxWidth > 1000.dp) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            Column(modifier = Modifier.weight(0.65f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                SchoolProfileSection(
                                    schoolName = schoolName,
                                    onSchoolNameChange = { schoolName = it },
                                    regNo = regNo,
                                    onRegNoChange = { regNo = it },
                                    motto = motto,
                                    onMottoChange = { motto = it }
                                )
                                ContactInfoSection(
                                    email = email,
                                    onEmailChange = { email = it },
                                    phone = phone,
                                    onPhoneChange = { phone = it },
                                    website = website,
                                    onWebsiteChange = { website = it },
                                    address = address,
                                    onAddressChange = { address = it }
                                )
                                BranchesSection()
                            }
                            Column(modifier = Modifier.weight(0.35f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                BrandingSection()
                                WorkingHoursSection()
                                MaintenanceModeSection()
                            }
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            SchoolProfileSection(
                                schoolName = schoolName,
                                onSchoolNameChange = { schoolName = it },
                                regNo = regNo,
                                onRegNoChange = { regNo = it },
                                motto = motto,
                                onMottoChange = { motto = it }
                            )
                            BrandingSection()
                            ContactInfoSection(
                                email = email,
                                onEmailChange = { email = it },
                                phone = phone,
                                onPhoneChange = { phone = it },
                                website = website,
                                onWebsiteChange = { website = it },
                                address = address,
                                onAddressChange = { address = it }
                            )
                            WorkingHoursSection()
                            BranchesSection()
                            MaintenanceModeSection()
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
    onSchoolNameChange: (String) -> Unit,
    regNo: String,
    onRegNoChange: (String) -> Unit,
    motto: String,
    onMottoChange: (String) -> Unit
) {
    SettingsCard(
        title = "School Profile",
        icon = Icons.AutoMirrored.Filled.Assignment,
        iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
        iconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SettingsTextField("School Name", schoolName, onSchoolNameChange, Modifier.weight(1f))
                SettingsTextField("Registration Number", regNo, onRegNoChange, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SettingsDropdownField("Affiliation Board", "Cambridge Assessment International Education", Modifier.weight(1f))
                SettingsTextField("Establishment Date", "1995-09-15", {}, Modifier.weight(1f))
            }
            SettingsTextArea("School Motto", motto, onMottoChange)
        }
    }
}

@Composable
private fun BrandingSection() {
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
                        .clickable { }
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
                        Icon(Icons.Default.School, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    AppText("Upload Square Logo", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    AppText("SVG, PNG up to 5MB", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
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
                            Box(modifier = Modifier.size(32.dp).background(Color(0xFF4F46E5), RoundedCornerShape(6.dp)))
                            AppText("#4F46E5", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Pro Tip
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primary
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                AppText("Pro Tip", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                AppText(
                    "Updating your brand color will automatically reflect across all student portals and invoice templates within 15 minutes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                    AppText("Learn about themes", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(16.dp).padding(start = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun ContactInfoSection(
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    website: String,
    onWebsiteChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit
) {
    SettingsCard(
        title = "Contact Information",
        icon = Icons.Default.AlternateEmail,
        iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
        iconContentColor = MaterialTheme.colorScheme.onTertiaryContainer
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SettingsTextField("Email Address", email, onEmailChange, Modifier.weight(1f))
                SettingsTextField("Phone Number", phone, onPhoneChange, Modifier.weight(1f))
            }
            SettingsTextField("Website", website, onWebsiteChange)
            SettingsTextArea("Full Address", address, onAddressChange, rows = 2)
        }
    }
}

@Composable
private fun WorkingHoursSection() {
    SettingsCard(
        title = "Working Hours",
        icon = Icons.Default.Schedule,
        iconContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        iconContentColor = MaterialTheme.colorScheme.primary
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            WorkingDayRow("Monday", "08:00", "16:00", true)
            WorkingDayRow("Tuesday", "08:00", "16:00", true)
            WorkingDayRow("Wednesday", "08:00", "16:00", true)
            WorkingDayRow("Thursday", "08:00", "16:00", true)
            WorkingDayRow("Friday", "08:00", "16:00", true)
            WorkingDayRow("Saturday", "09:00", "13:00", true)
            WorkingDayRow("Sunday", "", "", false)
        }
    }
}

@Composable
private fun WorkingDayRow(day: String, start: String, end: String, isOpen: Boolean) {
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
                    Checkbox(checked = isOpen, onCheckedChange = {})
                    AppText(day, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.widthIn(min = if (isCompact) 60.dp else 80.dp))
                }
                if (isOpen) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppText(start, style = if (isCompact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium)
                        AppText("—", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outlineVariant)
                        AppText(end, style = if (isCompact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    AppText("CLOSED", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun BranchesSection() {
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
                        AdaptiveIconButton(
                            label = "Add Branch",
                            icon = Icons.Default.AddLocationAlt,
                            onClick = { },
                            isMobile = true
                        )
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
                        AdaptiveIconButton(
                            label = "Add Branch",
                            icon = Icons.Default.AddLocationAlt,
                            onClick = { },
                            isMobile = false
                        )
                    }
                }
            }

            // Simple Table-like structure with horizontal scroll or card view
            BoxWithConstraints {
                val isMobile = maxWidth < 600.dp
                if (isMobile) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        BranchMobileCard("Main Campus", "HEADQUARTERS", "Silicon Valley, CA", "Sarah Johnson", true)
                        BranchMobileCard("Riverside Branch", "ELEMENTARY", "Austin, TX", "Michael Chen", true)
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
                        
                        BranchRow("Main Campus", "HEADQUARTERS", "Silicon Valley, CA", "Sarah Johnson", true)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        BranchRow("Riverside Branch", "ELEMENTARY", "Austin, TX", "Michael Chen", true)
                    }
                }
            }
        }
    }
}

@Composable
private fun BranchMobileCard(name: String, tag: String, location: String, contact: String, isActive: Boolean) {
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
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = {}) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(20.dp), tint = AcademicError) }
            }
        }
    }
}

@Composable
private fun BranchRow(name: String, tag: String, location: String, contact: String, isActive: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1.5f)) {
            AppText(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            AppText(tag, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, letterSpacing = 1.sp)
        }
        AppText(location, Modifier.weight(1.5f), style = MaterialTheme.typography.bodySmall)
        Row(Modifier.weight(1.5f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.size(24.dp).background(MaterialTheme.colorScheme.secondaryContainer, CircleShape))
            AppText(contact, style = MaterialTheme.typography.bodySmall)
        }
        Box(Modifier.weight(1f)) {
            val statusColor = if (isActive) AcademicSuccess else AcademicError
            StatusBadge(text = if (isActive) "Active" else "Inactive", color = statusColor)
        }
        Row(Modifier.weight(0.8f), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = {}) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.outline) }
            IconButton(onClick = {}) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp), tint = AcademicError) }
        }
    }
}

@Composable
private fun MaintenanceModeSection() {
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
            Switch(checked = false, onCheckedChange = {})
        }
    }
}

@Composable
private fun SettingsTextField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        AppText(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SettingsTextArea(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, rows: Int = 3) {
    Column(modifier = modifier) {
        AppText(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = rows,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SettingsDropdownField(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        AppText(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
            readOnly = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}
