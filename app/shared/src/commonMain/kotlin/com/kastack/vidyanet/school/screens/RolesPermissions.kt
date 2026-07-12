package com.kastack.vidyanet.school.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
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
import com.kastack.vidyanet.models.role.ModulePermissionDto
import com.kastack.vidyanet.models.role.PermissionAction
import com.kastack.vidyanet.models.role.RoleDto
import com.kastack.vidyanet.school.components.HeaderAction
import com.kastack.vidyanet.school.components.SchoolSettingsHeader
import com.kastack.vidyanet.school.viewModels.RolesPermissionsViewModel
import com.kastack.vidyanet.theme.*
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun RolesPermissions(
    viewModel: RolesPermissionsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        SchoolSettingsHeader(
            title = "Roles & Permissions",
            subtitle = "Manage user roles and define granular access across all modules.",
            breadcrumbs = listOf("Settings", "Roles & Permissions"),
            primaryAction = HeaderAction(
                label = "Save Changes",
                icon = Icons.Default.Save,
                onClick = viewModel::savePermissions,
                isLoading = uiState.isSaving
            )
        )

        if (uiState.isLoading && uiState.roles.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            BoxWithConstraints {
                if (maxWidth > 800.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Left Column: Role List
                        Column(
                            modifier = Modifier.weight(0.35f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppText("System Roles", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                IconButton(onClick = {}) {
                                    Icon(Icons.Default.FilterList, null, modifier = Modifier.size(20.dp))
                                }
                            }

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(uiState.roles) { role ->
                                    RoleCard(
                                        role = role,
                                        isSelected = uiState.selectedRole?.id == role.id,
                                        onClick = { viewModel.selectRole(role) }
                                    )
                                }
                            }
                        }

                        // Right Column: Permission Matrix
                        Column(
                            modifier = Modifier.weight(0.65f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            PermissionMatrix(
                                selectedRoleName = uiState.selectedRole?.roleName ?: "",
                                permissions = uiState.permissions,
                                onToggle = viewModel::togglePermission
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
                        // Role Selection (Horizontal on mobile?)
                        AppText("System Roles", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            uiState.roles.forEach { role ->
                                Box(modifier = Modifier.width(280.dp)) {
                                    RoleCard(
                                        role = role,
                                        isSelected = uiState.selectedRole?.id == role.id,
                                        onClick = { viewModel.selectRole(role) }
                                    )
                                }
                            }
                        }

                        // Permission Matrix (Full width)
                        PermissionMatrix(
                            selectedRoleName = uiState.selectedRole?.roleName ?: "",
                            permissions = uiState.permissions,
                            onToggle = viewModel::togglePermission
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
                    AppText("Permissions updated successfully", color = MaterialTheme.colorScheme.inverseOnSurface)
                }
            }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000.milliseconds)
                viewModel.resetSaveSuccess()
            }
        }
    }
}


@Composable
private fun RoleCard(
    role: RoleDto,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceContainerLowest,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (role.roleCode) {
                            "SUPER_ADMIN" -> Icons.Default.Shield
                            "TEACHER" -> Icons.Default.Person
                            "ACCOUNTANT" -> Icons.Default.Payments
                            "LIBRARIAN" -> Icons.AutoMirrored.Filled.LibraryBooks
                            else -> Icons.Default.Person
                        },
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Surface(
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = CircleShape
                ) {
                    AppText(
                        text = if (role.isSystemRole) "System" else "Custom",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            AppText(role.roleName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            AppText("Managed granular permissions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            if (isSelected) {
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText("Managing Permissions...", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = {}, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary) }
                        IconButton(onClick = {}, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionMatrix(
    selectedRoleName: String,
    permissions: List<ModulePermissionDto>,
    onToggle: (String, PermissionAction) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column {
            // Matrix Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                BoxWithConstraints {
                    val isMobile = maxWidth < 600.dp
                    if (isMobile) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AppText("Permissions:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                AppText(selectedRoleName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AppText("Permissions:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    AppText(selectedRoleName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                AppText("Select exactly what this role can see and do within each module.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            BoxWithConstraints(modifier = Modifier.weight(1f)) {
                val isMobile = maxWidth < 700.dp
                if (isMobile) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(permissions) { permission ->
                            PermissionMobileCard(permission, onToggle)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppText("MODULE / RESOURCE", modifier = Modifier.weight(0.35f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                            listOf("VIEW", "CREATE", "EDIT", "DELETE", "EXPORT").forEach { action ->
                                AppText(action, modifier = Modifier.weight(0.13f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline, textAlign = TextAlign.Center)
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        // Table Rows
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(permissions) { permission ->
                                PermissionRow(permission, onToggle)
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                    }
                }
            }

            // Matrix Footer
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                BoxWithConstraints {
                    val isMobile = maxWidth < 600.dp
                    if (isMobile) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Box(modifier = Modifier.size(6.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                                    AppText("Active", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                                    AppText("${permissions.count { it.actions.isNotEmpty() }} Modules Active", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                AppText("Reset to Defaults", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionMobileCard(
    permission: ModulePermissionDto,
    onToggle: (String, PermissionAction) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                imageVector = when (permission.moduleName) {
                    "DASHBOARD" -> Icons.Default.Dashboard
                    "STUDENTS" -> Icons.Default.Group
                    "ATTENDANCE" -> Icons.Default.CalendarMonth
                    "EXAMINATIONS" -> Icons.Default.Quiz
                    "LIBRARY" -> Icons.AutoMirrored.Filled.LibraryBooks
                    else -> Icons.Default.Settings
                },
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            AppText(permission.moduleName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PermissionAction.entries.forEach { action ->
                val isEnabled = true
                val isChecked = permission.actions.contains(action)
                
                Surface(
                    onClick = { if (isEnabled) onToggle(permission.moduleName, action) },
                    enabled = isEnabled,
                    shape = RoundedCornerShape(8.dp),
                    color = if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    border = BorderStroke(1.dp, if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { if (isEnabled) onToggle(permission.moduleName, action) },
                            enabled = isEnabled,
                            modifier = Modifier.size(20.dp)
                        )
                        AppText(
                            text = action.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
                            fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionRow(
    permission: ModulePermissionDto,
    onToggle: (String, PermissionAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(0.35f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(
                imageVector = when (permission.moduleName) {
                    "DASHBOARD" -> Icons.Default.Dashboard
                    "STUDENTS" -> Icons.Default.Group
                    "ATTENDANCE" -> Icons.Default.CalendarMonth
                    "EXAMINATIONS" -> Icons.Default.Quiz
                    "LIBRARY" -> Icons.AutoMirrored.Filled.LibraryBooks
                    else -> Icons.Default.Settings
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
            Column {
                AppText(permission.moduleName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                AppText(permission.description ?: "", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        PermissionAction.entries.forEach { action ->
            Box(modifier = Modifier.weight(0.13f), contentAlignment = Alignment.Center) {
                Checkbox(
                    checked = permission.actions.contains(action),
                    onCheckedChange = { onToggle(permission.moduleName, action) },
                    enabled = true
                )
            }
        }
    }
}
