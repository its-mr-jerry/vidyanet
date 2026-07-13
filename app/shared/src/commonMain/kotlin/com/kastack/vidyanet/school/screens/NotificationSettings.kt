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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kastack.vidyanet.commonUi.components.*
import com.kastack.vidyanet.models.settings.NotificationCategory
import com.kastack.vidyanet.models.settings.NotificationChannel
import com.kastack.vidyanet.models.settings.NotificationEventRule
import com.kastack.vidyanet.school.components.HeaderAction
import com.kastack.vidyanet.school.components.SchoolSettingsHeader
import com.kastack.vidyanet.school.components.StatusBadge
import com.kastack.vidyanet.school.viewModels.NotificationSettingsViewModel
import com.kastack.vidyanet.theme.*
import com.kastack.vidyanet.validators.ValidationSchema
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationSettings(
    schoolId: String,
    viewModel: NotificationSettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var templateError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(schoolId) {
        viewModel.loadSettings(schoolId)
    }

    if (uiState.error != null) {
        AppDialog(
            state = AppDialogState(
                isVisible = true,
                type = AppDialogType.ERROR,
                title = "Error",
                message = uiState.error ?: "",
                confirmLabel = "OK",
                onConfirm = viewModel::clearError
            ),
            onDismissRequest = viewModel::clearError
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Page Header
            SchoolSettingsHeader(
                title = "Notifications",
                subtitle = "Configure how and when users receive alerts via Email, SMS, and Push Notifications.",
                breadcrumbs = listOf("Settings", "Notifications"),
                primaryAction = if (uiState.canEdit) HeaderAction(
                    label = "Save Changes",
                    icon = Icons.Default.Save,
                    onClick = {
                        if (uiState.templateContent.isBlank()) {
                            templateError = "Message template cannot be empty"
                        } else if (uiState.templateContent.length > 1000) {
                            templateError = "Template exceeds 1000 characters"
                        } else {
                            viewModel.saveSettings()
                        }
                    },
                    isLoading = uiState.isSaving
                ) else null
            )

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                BoxWithConstraints {
                    if (maxWidth > 1100.dp) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Left Column: Global Channels & Rules
                            Column(
                                modifier = Modifier
                                    .weight(0.65f)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                // Global Channel Toggles
                                NotificationChannelsSection(
                                    emailEnabled = uiState.channels.emailEnabled,
                                    smsEnabled = uiState.channels.smsEnabled,
                                    pushEnabled = uiState.channels.pushEnabled,
                                    onToggle = viewModel::updateChannelEnabled,
                                    readOnly = !uiState.canEdit
                                )

                                // Event-Based Rules
                                EventRulesSection(
                                    rules = uiState.eventRules,
                                    onToggle = viewModel::toggleEventPermission,
                                    readOnly = !uiState.canEdit
                                )
                            }

                            // Right Column: Template Preview
                            Column(
                                modifier = Modifier.weight(0.35f),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                TemplatePreviewSection(
                                    selectedEventId = uiState.selectedEventId,
                                    selectedChannel = uiState.selectedChannel,
                                    templateContent = uiState.templateContent,
                                    eventRules = uiState.eventRules,
                                    onEventSelect = { 
                                        templateError = null
                                        viewModel.selectTemplate(it, uiState.selectedChannel) 
                                    },
                                    onChannelSelect = { 
                                        templateError = null
                                        viewModel.selectTemplate(uiState.selectedEventId, it) 
                                    },
                                    onContentChange = {
                                        templateError = null
                                        viewModel.updateTemplateContent(it)
                                    },
                                    templateError = templateError,
                                    readOnly = !uiState.canEdit
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Global Channel Toggles Adaptive
                            NotificationChannelsSectionAdaptive(
                                emailEnabled = uiState.channels.emailEnabled,
                                smsEnabled = uiState.channels.smsEnabled,
                                pushEnabled = uiState.channels.pushEnabled,
                                onToggle = viewModel::updateChannelEnabled,
                                readOnly = !uiState.canEdit
                            )

                            // Event-Based Rules
                            EventRulesSection(
                                rules = uiState.eventRules,
                                onToggle = viewModel::toggleEventPermission,
                                readOnly = !uiState.canEdit
                            )

                            // Template Preview
                            TemplatePreviewSection(
                                selectedEventId = uiState.selectedEventId,
                                selectedChannel = uiState.selectedChannel,
                                templateContent = uiState.templateContent,
                                eventRules = uiState.eventRules,
                                onEventSelect = { 
                                    templateError = null
                                    viewModel.selectTemplate(it, uiState.selectedChannel) 
                                },
                                onChannelSelect = { 
                                    templateError = null
                                    viewModel.selectTemplate(uiState.selectedEventId, it) 
                                },
                                onContentChange = {
                                    templateError = null
                                    viewModel.updateTemplateContent(it)
                                },
                                templateError = templateError,
                                readOnly = !uiState.canEdit
                            )
                        }
                    }
                }
            }
        }

        if (uiState.saveSuccess) {
            Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.inverseSurface,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primaryContainer)
                        AppText("Settings saved successfully!", color = MaterialTheme.colorScheme.inverseOnSurface)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationChannelsSectionAdaptive(
    emailEnabled: Boolean,
    smsEnabled: Boolean,
    pushEnabled: Boolean,
    onToggle: (NotificationChannel, Boolean) -> Unit,
    readOnly: Boolean
) {
    BoxWithConstraints {
        if (maxWidth > 600.dp) {
            NotificationChannelsSection(emailEnabled, smsEnabled, pushEnabled, onToggle, readOnly)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ChannelCard(
                    title = "Email Service",
                    description = "Global toggle for all automated emails.",
                    icon = Icons.Default.Mail,
                    enabled = emailEnabled,
                    onToggle = { onToggle(NotificationChannel.EMAIL, it) },
                    readOnly = readOnly
                )
                ChannelCard(
                    title = "SMS Gateway",
                    description = "Direct SMS alerts via Twilio provider.",
                    icon = Icons.Default.Sms,
                    enabled = smsEnabled,
                    onToggle = { onToggle(NotificationChannel.SMS, it) },
                    readOnly = readOnly
                )
                ChannelCard(
                    title = "App Push",
                    description = "Real-time mobile app notifications.",
                    icon = Icons.Default.NotificationsActive,
                    enabled = pushEnabled,
                    onToggle = { onToggle(NotificationChannel.PUSH, it) },
                    readOnly = readOnly
                )
            }
        }
    }
}


@Composable
private fun NotificationChannelsSection(
    emailEnabled: Boolean,
    smsEnabled: Boolean,
    pushEnabled: Boolean,
    onToggle: (NotificationChannel, Boolean) -> Unit,
    readOnly: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        ChannelCard(
            title = "Email Service",
            description = "Global toggle for all automated emails.",
            icon = Icons.Default.Mail,
            enabled = emailEnabled,
            onToggle = { onToggle(NotificationChannel.EMAIL, it) },
            modifier = Modifier.weight(1f),
            readOnly = readOnly
        )
        ChannelCard(
            title = "SMS Gateway",
            description = "Direct SMS alerts via Twilio provider.",
            icon = Icons.Default.Sms,
            enabled = smsEnabled,
            onToggle = { onToggle(NotificationChannel.SMS, it) },
            modifier = Modifier.weight(1f),
            readOnly = readOnly
        )
        ChannelCard(
            title = "App Push",
            description = "Real-time mobile app notifications.",
            icon = Icons.Default.NotificationsActive,
            enabled = pushEnabled,
            onToggle = { onToggle(NotificationChannel.PUSH, it) },
            modifier = Modifier.weight(1f),
            readOnly = readOnly
        )
    }
}

@Composable
private fun ChannelCard(
    title: String,
    description: String,
    icon: ImageVector,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier.size(40.dp).background(if (enabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                }
                Switch(checked = enabled, onCheckedChange = onToggle, enabled = !readOnly)
            }
            Column {
                AppText(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                AppText(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun EventRulesSection(
    rules: List<NotificationEventRule>,
    onToggle: (String, NotificationChannel, Boolean) -> Unit,
    readOnly: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = Color.White
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLowest).padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText("Event Rules", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(4.dp)) {
                    AppText("MASTER CONTROLS", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            NotificationCategory.entries.forEach { category ->
                val categoryRules = rules.filter { it.category == category }
                if (categoryRules.isNotEmpty()) {
                    CategoryGroup(
                        category = category,
                        rules = categoryRules,
                        onToggle = onToggle,
                        readOnly = readOnly
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryGroup(
    category: NotificationCategory,
    rules: List<NotificationEventRule>,
    onToggle: (String, NotificationChannel, Boolean) -> Unit,
    readOnly: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.3f)).padding(24.dp)) {
        AppText(category.name + " EVENTS", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, letterSpacing = 1.sp)
        Spacer(Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            rules.forEach { rule ->
                EventRuleRow(rule, onToggle, readOnly)
            }
        }
    }
}

@Composable
private fun EventRuleRow(
    rule: NotificationEventRule,
    onToggle: (String, NotificationChannel, Boolean) -> Unit,
    readOnly: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when(rule.id) {
                            "exam_result" -> Icons.Default.School
                            "student_attendance" -> Icons.Default.CalendarMonth
                            "fee_due" -> Icons.Default.Payments
                            "staff_meeting" -> Icons.Default.Badge
                            else -> Icons.Default.Notifications
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
                Column {
                    AppText(rule.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    AppText(rule.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                ChannelToggle("EMAIL", rule.emailEnabled, readOnly) { onToggle(rule.id, NotificationChannel.EMAIL, it) }
                ChannelToggle("SMS", rule.smsEnabled, readOnly) { onToggle(rule.id, NotificationChannel.SMS, it) }
                ChannelToggle("PUSH", rule.pushEnabled, readOnly) { onToggle(rule.id, NotificationChannel.PUSH, it) }
            }
        }
    }
}

@Composable
private fun ChannelToggle(label: String, checked: Boolean, readOnly: Boolean, onToggle: (Boolean) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        AppText(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
        Checkbox(checked = checked, onCheckedChange = onToggle, enabled = !readOnly)
    }
}

@Composable
private fun TemplatePreviewSection(
    selectedEventId: String,
    selectedChannel: NotificationChannel,
    templateContent: String,
    eventRules: List<NotificationEventRule>,
    onEventSelect: (String) -> Unit,
    onChannelSelect: (NotificationChannel) -> Unit,
    onContentChange: (String) -> Unit,
    templateError: String?,
    readOnly: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = Color.White
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLowest).padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.EditNote, null, tint = MaterialTheme.colorScheme.primary)
                AppText("Template Preview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppFormDropdown(
                        label = "Selected Event",
                        value = eventRules.find { it.id == selectedEventId }?.name ?: "Select Event",
                        options = eventRules.map { it.name },
                        onOptionSelected = { name ->
                            eventRules.find { it.name == name }?.let { onEventSelect(it.id) }
                        }
                    )
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppText("Channel Type", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    ) {
                        NotificationChannel.entries.forEach { channel ->
                            val isSelected = selectedChannel == channel
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) Color.White else Color.Transparent,
                                shadowElevation = if (isSelected) 2.dp else 0.dp,
                                onClick = { onChannelSelect(channel) }
                            ) {
                                Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    AppText(
                                        text = channel.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Phone Mockup
                PhoneMockup(templateContent, Modifier.align(Alignment.CenterHorizontally))
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppTextArea(
                        schema = com.kastack.vidyanet.validators.FieldSchema("Message Template", isRequired = true),
                        value = templateContent,
                        onValueChange = onContentChange,
                        readOnly = readOnly,
                        rows = 6,
                        error = templateError
                    )
                    
                    if (!readOnly) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("[Student_Name]", "[Date]", "[Parent_Name]").forEach { placeholder ->
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(4.dp),
                                    onClick = { onContentChange(templateContent + " " + placeholder) }
                                ) {
                                    AppText(placeholder, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                
                if (!readOnly) {
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        AppText("Send Test Notification", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PhoneMockup(content: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(240.dp)
            .height(320.dp)
            .background(Color(0xFF111C2D), RoundedCornerShape(32.dp))
            .border(6.dp, Color(0xFF263143), RoundedCornerShape(32.dp))
            .padding(16.dp)
    ) {
        // Notch
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(4.dp)
                .background(Color(0xFF263143), RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .align(Alignment.TopCenter)
        )
        
        Column(modifier = Modifier.padding(top = 24.dp)) {
            Surface(
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(16.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.School, null, tint = Color.White, modifier = Modifier.size(10.dp))
                            }
                            AppText("VidyaNet", color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        AppText("Now", color = Color.White.copy(alpha = 0.4f), style = MaterialTheme.typography.labelSmall)
                    }
                    Column {
                        AppText("Attendance Alert", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        AppText(content, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelSmall, lineHeight = 14.sp)
                    }
                }
            }
        }
    }
}
