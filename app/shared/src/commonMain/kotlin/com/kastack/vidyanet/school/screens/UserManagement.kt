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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kastack.vidyanet.commonUi.components.AppDialog
import com.kastack.vidyanet.commonUi.components.AppDialogState
import com.kastack.vidyanet.commonUi.components.AppDialogType
import com.kastack.vidyanet.commonUi.components.AppPagination
import com.kastack.vidyanet.commonUi.components.AppTextField
import com.kastack.vidyanet.commonUi.components.AppText
import com.kastack.vidyanet.models.user.UserStatus
import com.kastack.vidyanet.school.components.HeaderAction
import com.kastack.vidyanet.school.components.SchoolSettingsHeader
import com.kastack.vidyanet.school.components.StatusBadge
import com.kastack.vidyanet.school.viewModels.PendingInvite
import com.kastack.vidyanet.school.viewModels.UserManagementViewModel
import com.kastack.vidyanet.school.viewModels.UserUiModel
import com.kastack.vidyanet.theme.AcademicError
import com.kastack.vidyanet.theme.AcademicSuccess
import com.kastack.vidyanet.validators.ValidationSchema
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserManagement(
    schoolId: String,
    viewModel: UserManagementViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showInviteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(schoolId) {
        viewModel.init(schoolId)
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

    if (showInviteDialog) {
        InviteUserDialog(
            roles = uiState.roles,
            onDismiss = { showInviteDialog = false },
            onInvite = { name, phone, email, roleIds ->
                viewModel.createUser(name, phone, email, roleIds)
                showInviteDialog = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Navigation / Header
        SchoolSettingsHeader(
            title = "User Management",
            subtitle = "Manage staff, teacher, and administrator accounts and access levels.",
            breadcrumbs = listOf("Settings", "User Management"),
            primaryAction = if (uiState.canEdit) HeaderAction(
                label = "Invite User",
                icon = Icons.Default.Add,
                onClick = { showInviteDialog = true }
            ) else null,
            secondaryAction = if (uiState.canEdit) HeaderAction(
                label = "Bulk Invite",
                icon = Icons.Default.UploadFile,
                onClick = { /* Bulk logic */ }
            ) else null
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Filter Bar
            FilterBar(
                searchQuery = uiState.searchQuery,
                onSearchChange = viewModel::onSearchQueryChanged,
                roles = uiState.roles,
                selectedRoleId = uiState.selectedRoleId,
                onRoleChange = viewModel::onRoleFilterChanged,
                selectedStatus = uiState.selectedStatus,
                onStatusChange = viewModel::onStatusFilterChanged
            )

            BoxWithConstraints {
                if (maxWidth > 1000.dp) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        // User Table Section
                        Column(modifier = Modifier.weight(0.65f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            UserTable(
                                users = uiState.users,
                                totalCount = uiState.totalUsers,
                                currentPage = uiState.currentPage,
                                rowsPerPage = uiState.rowsPerPage,
                                onPageChange = viewModel::onPageChanged,
                                onRowsPerPageChange = viewModel::onRowsPerPageChanged,
                                onToggleStatus = viewModel::toggleUserStatus,
                                onDelete = viewModel::deleteUser,
                                canEdit = uiState.canEdit
                            )
                        }

                        // Right Sidebar Content
                        Column(modifier = Modifier.weight(0.35f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            PendingInvitesSection(uiState.pendingInvites)
                            RolesSummaryCard(uiState.roles)
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        UserTable(
                            users = uiState.users,
                            totalCount = uiState.totalUsers,
                            currentPage = uiState.currentPage,
                            rowsPerPage = uiState.rowsPerPage,
                            onPageChange = viewModel::onPageChanged,
                            onRowsPerPageChange = viewModel::onRowsPerPageChanged,
                            onToggleStatus = viewModel::toggleUserStatus,
                            onDelete = viewModel::deleteUser,
                            canEdit = uiState.canEdit
                        )
                        PendingInvitesSection(uiState.pendingInvites)
                        RolesSummaryCard(uiState.roles)
                    }
                }
            }
        }
    }
}


@Composable
private fun FilterBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    roles: List<com.kastack.vidyanet.models.role.RoleDto>,
    selectedRoleId: Long?,
    onRoleChange: (Long?) -> Unit,
    selectedStatus: UserStatus?,
    onStatusChange: (UserStatus?) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { AppText("Search by name, phone or email...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                singleLine = true
            )
            
            var roleMenuExpanded by remember { mutableStateOf(false) }
            Box {
                FilterDropdown(
                    label = "Role: ${roles.find { it.id == selectedRoleId }?.roleName ?: "All"}",
                    onClick = { roleMenuExpanded = true }
                )
                DropdownMenu(expanded = roleMenuExpanded, onDismissRequest = { roleMenuExpanded = false }) {
                    DropdownMenuItem(text = { Text("All Roles") }, onClick = { onRoleChange(null); roleMenuExpanded = false })
                    roles.forEach { role ->
                        DropdownMenuItem(text = { Text(role.roleName) }, onClick = { onRoleChange(role.id); roleMenuExpanded = false })
                    }
                }
            }

            var statusMenuExpanded by remember { mutableStateOf(false) }
            Box {
                FilterDropdown(
                    label = "Status: ${selectedStatus?.name ?: "All"}",
                    onClick = { statusMenuExpanded = true }
                )
                DropdownMenu(expanded = statusMenuExpanded, onDismissRequest = { statusMenuExpanded = false }) {
                    DropdownMenuItem(text = { Text("All Status") }, onClick = { onStatusChange(null); statusMenuExpanded = false })
                    UserStatus.entries.forEach { status ->
                        DropdownMenuItem(text = { Text(status.name) }, onClick = { onStatusChange(status); statusMenuExpanded = false })
                    }
                }
            }
            
            IconButton(
                onClick = { onSearchChange(""); onRoleChange(null); onStatusChange(null) },
                modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.Refresh, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            VerticalDivider(modifier = Modifier.height(32.dp), color = MaterialTheme.colorScheme.outlineVariant)
            
            TextButton(onClick = {}) {
                Icon(Icons.Default.Download, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                AppText("Export CSV", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FilterDropdown(label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppText(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun UserTable(
    users: List<UserUiModel>,
    totalCount: Int,
    currentPage: Int,
    rowsPerPage: Int,
    onPageChange: (Int) -> Unit,
    onRowsPerPageChange: (Int) -> Unit,
    onToggleStatus: (UserUiModel) -> Unit,
    onDelete: (Long) -> Unit,
    canEdit: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f)).padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText("Active Users", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = CircleShape
                ) {
                    AppText("$totalCount Total", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            BoxWithConstraints {
                val viewportWidth = maxWidth
                val isMobile = maxWidth < 700.dp
                if (isMobile) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        users.forEach { user ->
                            UserMobileCard(user, onToggleStatus, onDelete, canEdit)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                        val tableWidth = if (viewportWidth > 800.dp) viewportWidth else 800.dp
                        Column(modifier = Modifier.width(tableWidth)) {
                            // Table Header
                            Row(
                                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.3f)).padding(horizontal = 24.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppText("USER", Modifier.weight(1.5f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                AppText("ROLE", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                AppText("LAST LOGIN", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                AppText("STATUS", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.width(100.dp))
                            }
                            
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            
                            users.forEach { user ->
                                UserTableRow(user, onToggleStatus, onDelete, canEdit)
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                    }
                }
            }
            
            // Pagination
            AppPagination(
                totalItems = totalCount,
                currentPage = currentPage,
                rowsPerPage = rowsPerPage,
                onPageChange = onPageChange,
                onRowsPerPageChange = onRowsPerPageChange
            )
        }
    }
}

@Composable
private fun UserMobileCard(
    user: UserUiModel,
    onToggleStatus: (UserUiModel) -> Unit,
    onDelete: (Long) -> Unit,
    canEdit: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = user.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").take(2).uppercase()
                    AppText(initials, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Column {
                    AppText(user.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    AppText(user.email, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            val statusColor = if (user.status == UserStatus.ACTIVE) AcademicSuccess else MaterialTheme.colorScheme.outlineVariant
            StatusBadge(text = if (user.status == UserStatus.ACTIVE) "Active" else "Inactive", color = statusColor)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = CircleShape) {
                AppText(
                    text = user.role, 
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp), 
                    style = MaterialTheme.typography.labelSmall, 
                    fontWeight = FontWeight.Black, 
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            AppText("Last Login: ${user.lastLogin}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            if (canEdit) {
                IconButton(onClick = {}) { Icon(Icons.Default.LockReset, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                IconButton(onClick = { onToggleStatus(user) }) { Icon(if (user.status == UserStatus.ACTIVE) Icons.Default.PersonOff else Icons.Default.Check, null, modifier = Modifier.size(18.dp), tint = if (user.status == UserStatus.ACTIVE) AcademicError else MaterialTheme.colorScheme.primary) }
                IconButton(onClick = { onDelete(user.id) }) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp), tint = AcademicError) }
            } else {
                IconButton(onClick = {}) { Icon(Icons.Default.Visibility, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@Composable
private fun UserTableRow(
    user: UserUiModel,
    onToggleStatus: (UserUiModel) -> Unit,
    onDelete: (Long) -> Unit,
    canEdit: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1.5f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (user.avatarUrl != null) {
                    // Coil image would go here
                } else {
                    val initials = user.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").take(2).uppercase()
                    AppText(initials, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Column {
                AppText(user.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                AppText(user.email, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        Box(modifier = Modifier.weight(1f)) {
            Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = CircleShape) {
                AppText(user.role, modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            }
        }
        
        AppText(user.lastLogin, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val statusColor = if (user.status == UserStatus.ACTIVE) AcademicSuccess else MaterialTheme.colorScheme.outlineVariant
            Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
            AppText(if (user.status == UserStatus.ACTIVE) "Active" else "Inactive", style = MaterialTheme.typography.bodySmall)
        }
        
        Row(modifier = Modifier.width(100.dp), horizontalArrangement = Arrangement.End) {
            if (canEdit) {
                IconButton(onClick = {}) { Icon(Icons.Default.LockReset, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                IconButton(onClick = { onToggleStatus(user) }) { Icon(if (user.status == UserStatus.ACTIVE) Icons.Default.PersonOff else Icons.Default.Check, null, modifier = Modifier.size(18.dp), tint = if (user.status == UserStatus.ACTIVE) AcademicError else MaterialTheme.colorScheme.primary) }
                IconButton(onClick = { onDelete(user.id) }) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp), tint = AcademicError) }
            } else {
                IconButton(onClick = {}) { Icon(Icons.Default.Visibility, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@Composable
private fun PendingInvitesSection(invites: List<PendingInvite>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f)).padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText("Pending Invites", style = MaterialTheme.typography.headlineSmall, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.Mail, null, tint = MaterialTheme.colorScheme.primary)
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            Column(modifier = Modifier.fillMaxWidth()) {
                invites.forEach { invite ->
                    PendingInviteRow(invite)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
            
            Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.3f)).padding(12.dp), contentAlignment = Alignment.Center) {
                TextButton(onClick = {}) {
                    AppText("View All Invitations", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun PendingInviteRow(invite: PendingInvite) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column {
                AppText(invite.email, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppText("Invited as ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    AppText(invite.role, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(4.dp)) {
                AppText("PENDING", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            AppText(invite.sentAt, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            TextButton(onClick = {}, contentPadding = PaddingValues(0.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(14.dp))
                    AppText("Resend", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun RolesSummaryCard(roles: List<com.kastack.vidyanet.models.role.RoleDto>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AdminPanelSettings, null, tint = Color.White)
                }
                AppText("Roles Summary", style = MaterialTheme.typography.headlineSmall, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            
            roles.take(5).forEach { role ->
                SummaryItem(role.roleName, (5..50).random()) // Mock count
            }
            
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                AppText("Manage Role Definitions", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, count: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        AppText(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        AppText(count.toString(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun InviteUserDialog(
    roles: List<com.kastack.vidyanet.models.role.RoleDto>,
    onDismiss: () -> Unit,
    onInvite: (name: String, phone: String, email: String, roleIds: List<Long>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val selectedRoleIds = remember { mutableStateListOf<Long>() }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { AppText("Invite User", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppTextField(
                    schema = ValidationSchema.user.fullName,
                    value = name,
                    onValueChange = { name = it },
                )
                AppTextField(
                    schema = ValidationSchema.user.phone,
                    value = phone,
                    onValueChange = { phone = it },
                )
                AppTextField(
                    schema = ValidationSchema.user.email,
                    value = email,
                    onValueChange = { email = it },
                )
                
                AppText("Assign Roles (Multiple Allowed)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                roles.forEach { role ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { 
                            if (selectedRoleIds.contains(role.id)) selectedRoleIds.remove(role.id)
                            else selectedRoleIds.add(role.id)
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedRoleIds.contains(role.id),
                            onCheckedChange = { 
                                if (it) selectedRoleIds.add(role.id)
                                else selectedRoleIds.remove(role.id)
                            }
                        )
                        AppText(role.roleName)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank() && phone.isNotBlank() && selectedRoleIds.isNotEmpty()) onInvite(name, phone, email, selectedRoleIds.toList()) }) {
                AppText("Invite")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                AppText("Cancel")
            }
        }
    )
}
