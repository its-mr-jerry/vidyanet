@file:Suppress("DEPRECATION")

package com.kastack.vidyanet.school.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kastack.vidyanet.commonUi.components.AppText
import com.kastack.vidyanet.models.settings.ApiKeyDto
import com.kastack.vidyanet.models.settings.IntegrationCategory
import com.kastack.vidyanet.models.settings.IntegrationDto
import com.kastack.vidyanet.school.components.AdaptiveIconButton
import com.kastack.vidyanet.school.components.HeaderAction
import com.kastack.vidyanet.school.components.SchoolSettingsHeader
import com.kastack.vidyanet.school.components.StatusBadge
import com.kastack.vidyanet.school.viewModels.IntegrationsViewModel
import com.kastack.vidyanet.theme.*
import com.kastack.vidyanet.utils.toKotlinx
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Integrations(
    viewModel: IntegrationsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SchoolSettingsHeader(
            title = "Integrations & API",
            subtitle = "Connect EduCore with external tools and manage API access.",
            breadcrumbs = listOf("Settings", "Integrations")
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Integration Categories
            IntegrationCategorySection(
                title = "Communication",
                icon = Icons.Default.Forum,
                integrations = uiState.integrations.filter { it.category == IntegrationCategory.COMMUNICATION },
                onConnect = viewModel::connectIntegration,
                onConfigure = viewModel::configureIntegration
            )

            IntegrationCategorySection(
                title = "Payments",
                icon = Icons.Default.Payments,
                integrations = uiState.integrations.filter { it.category == IntegrationCategory.PAYMENTS },
                onConnect = viewModel::connectIntegration,
                onConfigure = viewModel::configureIntegration
            )

            // API Key Management Section
            ApiKeyManagementSection(
                apiKeys = uiState.apiKeys,
                onGenerate = viewModel::generateNewKey,
                onRevoke = viewModel::revokeKey
            )

            // Documentation & Webhooks
            BottomCardsSection()
        }
    }
}


@Composable
private fun IntegrationCategorySection(
    title: String,
    icon: ImageVector,
    integrations: List<IntegrationDto>,
    onConnect: (String) -> Unit,
    onConfigure: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            AppText(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        BoxWithConstraints {
            if (maxWidth > 900.dp) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    integrations.forEach { integration ->
                        IntegrationCard(
                            integration = integration,
                            onConnect = { onConnect(integration.id) },
                            onConfigure = { onConfigure(integration.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill remaining space with placeholders if needed for consistent grid
                    repeat(3 - integrations.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    integrations.forEach { integration ->
                        IntegrationCard(
                            integration = integration,
                            onConnect = { onConnect(integration.id) },
                            onConfigure = { onConfigure(integration.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IntegrationCard(
    integration: IntegrationDto,
    onConnect: () -> Unit,
    onConfigure: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = getBrandColor(integration.id).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIntegrationIcon(integration.id),
                        contentDescription = null,
                        tint = getBrandColor(integration.id),
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                if (integration.isConnected) {
                    StatusBadge("Connected", AcademicSuccess)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                AppText(integration.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                AppText(integration.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, minLines = 2)
            }

            if (integration.isConnected) {
                OutlinedButton(
                    onClick = onConfigure,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    AppText("Configure", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onConnect,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    AppText("Connect", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun getIntegrationIcon(id: String): ImageVector = when(id) {
    "whatsapp" -> Icons.Default.Chat
    "twilio" -> Icons.Default.Sms
    "sendgrid" -> Icons.Default.Mail
    "stripe" -> Icons.Default.AccountBalance
    "razorpay" -> Icons.Default.CurrencyRupee
    else -> Icons.Default.Extension
}

private fun getBrandColor(id: String): Color = when(id) {
    "whatsapp" -> Color(0xFF25D366)
    "twilio" -> Color(0xFFF22F46)
    "sendgrid" -> Color(0xFF1A82E2)
    "stripe" -> Color(0xFF635BFF)
    "razorpay" -> Color(0xFF3395FF)
    else -> Color.Gray
}

@Composable
private fun ApiKeyManagementSection(
    apiKeys: List<ApiKeyDto>,
    onGenerate: () -> Unit,
    onRevoke: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            BoxWithConstraints {
                val isCompact = maxWidth < 700.dp
                if (maxWidth > 600.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            AppText("API Key Management", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            AppText("Authenticated keys for direct access to EduCore ERP endpoints.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        AdaptiveIconButton(
                            label = "Generate New Key",
                            icon = Icons.Default.Add,
                            onClick = onGenerate,
                            isMobile = isCompact,
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column {
                            AppText("API Key Management", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            AppText("Authenticated keys for direct access to EduCore ERP endpoints.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        AdaptiveIconButton(
                            label = "Generate New Key",
                            icon = Icons.Default.Add,
                            onClick = onGenerate,
                            isMobile = true,
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                color = MaterialTheme.colorScheme.surfaceContainerLowest
            ) {
                BoxWithConstraints {
                    val viewportWidth = maxWidth
                    val isMobile = maxWidth < 700.dp
                    if (isMobile) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            apiKeys.forEach { key ->
                                ApiKeyMobileCard(key, onRevoke = { onRevoke(key.id) })
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            }
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                            val tableWidth = if (viewportWidth > 800.dp) viewportWidth else 800.dp
                            Column(modifier = Modifier.width(tableWidth)) {
                                // Table Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f))
                                        .padding(horizontal = 24.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AppText("KEY LABEL", Modifier.weight(1.5f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    AppText("CREATED", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    AppText("LAST USED", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    AppText("PERMISSIONS", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(80.dp))
                                }
                                
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                
                                apiKeys.forEach { key ->
                                    ApiKeyRow(key, onRevoke = { onRevoke(key.id) })
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
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
private fun ApiKeyMobileCard(key: ApiKeyDto, onRevoke: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.VpnKey, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                AppText(key.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
            Surface(
                color = if (key.permissions == "Read/Write") MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(4.dp)
            ) {
                AppText(
                    text = key.permissions, 
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), 
                    style = MaterialTheme.typography.labelSmall, 
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            val dtCreated = key.createdAt.toKotlinx().toLocalDateTime(TimeZone.currentSystemDefault())
            Column {
                AppText("Created", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                AppText("${dtCreated.month.name.take(3)} ${dtCreated.day}, ${dtCreated.year}", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                AppText("Last Used", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                AppText(if (key.lastUsedAt != null) "2 mins ago" else "-", style = MaterialTheme.typography.bodySmall)
            }
        }
        
        Button(
            onClick = onRevoke,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(8.dp)
        ) {
            AppText("Revoke API Key", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ApiKeyRow(key: ApiKeyDto, onRevoke: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(Modifier.weight(1.5f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.VpnKey, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            AppText(key.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
        
        val dtCreated = key.createdAt.toKotlinx().toLocalDateTime(TimeZone.currentSystemDefault())
        AppText("${dtCreated.month.name.take(3)} ${dtCreated.day}, ${dtCreated.year}", Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        AppText(
            text = if (key.lastUsedAt != null) "2 mins ago" else "-", // Simple mock relative time
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Box(Modifier.weight(1f)) {
            Surface(
                color = if (key.permissions == "Read/Write") MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(4.dp)
            ) {
                AppText(key.permissions, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
            }
        }
        
        TextButton(onClick = onRevoke, modifier = Modifier.width(80.dp)) {
            AppText("Revoke", style = MaterialTheme.typography.labelLarge, color = AcademicError, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BottomCardsSection() {
    BoxWithConstraints {
        if (maxWidth > 900.dp) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                DocumentationCard(Modifier.weight(1f))
                WebhooksCard(Modifier.weight(1f))
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                DocumentationCard(Modifier.fillMaxWidth())
                WebhooksCard(Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun DocumentationCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Decorative icon
            Icon(
                Icons.Default.Code,
                null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 16.dp, y = 16.dp)
                    .size(120.dp),
                tint = Color.White.copy(alpha = 0.1f)
            )
            
            Row(modifier = Modifier.padding(24.dp), horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(64.dp).background(Color.White.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Terminal, null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppText("API Documentation", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    AppText("Explore our comprehensive REST API endpoints and SDKs for deeper customization.", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                        AppText("View Docs", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color.White)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(16.dp).padding(start = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun WebhooksCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(24.dp), horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Hub, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(32.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppText("Webhooks", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                AppText("Subscribe to real-time events like student registration, fee payment, or exam results.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                    AppText("Manage Webhooks", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp).padding(start = 4.dp))
                }
            }
        }
    }
}
