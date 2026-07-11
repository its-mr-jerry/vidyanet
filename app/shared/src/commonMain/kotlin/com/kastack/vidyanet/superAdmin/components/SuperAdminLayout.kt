package com.kastack.vidyanet.superAdmin.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kastack.vidyanet.commonUi.components.AppText

@Composable
fun SuperAdminLayout(
    currentDestination: String,
    onNavigate: (String) -> Unit,
    academicYear: String = "2023-24",
    content: @Composable (PaddingValues) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        Sidebar(
            currentDestination = currentDestination,
            onNavigate = onNavigate,
            modifier = Modifier.width(280.dp).fillMaxHeight()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(academicYear = academicYear)
            Box(modifier = Modifier.fillMaxSize()) {
                content(PaddingValues(0.dp))
            }
        }
    }
}

@Composable
private fun Sidebar(
    currentDestination: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            AppText(
                text = "VidyaNet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            AppText(
                text = "Super Admin Portal",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alpha(0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.verticalScroll(rememberScrollState()).weight(1f)) {
                SidebarItem(
                    label = "Dashboard",
                    icon = Icons.Default.Dashboard,
                    active = currentDestination == "Dashboard",
                    onClick = { onNavigate("Dashboard") }
                )
                SidebarItem(
                    label = "Schools",
                    icon = Icons.Default.Domain,
                    active = currentDestination == "Schools",
                    onClick = { onNavigate("Schools") }
                )

                Spacer(modifier = Modifier.height(24.dp))
                AppText(
                    text = "MANAGEMENT",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.alpha(0.4f),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                SidebarItem("Fees & Finance", Icons.Default.Payments, active = false, onClick = {})
                SidebarItem("Reports & Analytics", Icons.Default.Analytics, active = false, onClick = {})
                SidebarItem("System Settings", Icons.Default.Settings, active = false, onClick = {})
            }
        }
    }
}

@Composable
private fun SidebarItem(
    label: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (active) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            AppText(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                color = if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun TopBar(academicYear: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(64.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side (Title or breadcrumbs can go here if needed)
            Box(modifier = Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AppText(
                    text = "Academic Year: $academicYear",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape).padding(horizontal = 12.dp, vertical = 4.dp)
                )
                IconButton(onClick = {}) {
                    BadgedBox(badge = { Badge { AppText("") } }) {
                        Icon(Icons.Outlined.Notifications, null)
                    }
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Outlined.Settings, null)
                }
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outlineVariant))
            }
        }
    }
}
