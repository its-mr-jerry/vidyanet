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
import com.kastack.vidyanet.school.viewModels.UserManagementViewModel
import com.kastack.vidyanet.school.viewModels.UserUiModel
import com.kastack.vidyanet.theme.AcademicError
import com.kastack.vidyanet.theme.AcademicSuccess
import com.kastack.vidyanet.validators.ValidationSchema
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserManagement(
    schoolId: String,
    viewModel: UserManagementViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var showAddUserDialog by remember { mutableStateOf(false) }
    var showFormatGuide by remember { mutableStateOf(false) }

    val csvLauncher = rememberFilePickerLauncher(
        type = PickerType.File(extensions = listOf("csv")),
        mode = PickerMode.Single
    ) { file ->
        file?.let {
            scope.launch {
                val bytes = it.readBytes()
                val content = bytes.decodeToString()
                viewModel.importUsersFromCsv(content)
            }
        }
    }

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

    if (showFormatGuide) {
        CsvFormatGuideDialog(
            roles = uiState.roles,
            onDismiss = { showFormatGuide = false }
        )
    }

    if (showAddUserDialog) {
        AddUserDialog(
            roles = uiState.roles,
            onDismiss = { showAddUserDialog = false },
            onAdd = { name, phone, email, roleIds ->
                viewModel.createUser(name, phone, email, roleIds)
                showAddUserDialog = false
            }
        )
    }

    uiState.editingUser?.let { user ->
        EditUserDialog(
            user = user,
            roles = uiState.roles,
            onDismiss = { viewModel.setEditingUser(null) },
            onSave = { name, email, roleIds ->
                viewModel.updateUser(user.id, name, email, roleIds)
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
                label = "Add User",
                icon = Icons.Default.Add,
                onClick = { showAddUserDialog = true }
            ) else null,
            secondaryAction = if (uiState.canEdit) HeaderAction(
                label = "Import CSV",
                icon = Icons.Default.UploadFile,
                onClick = { csvLauncher.launch() }
            ) else null
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isMobile = maxWidth < 600.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isMobile) 12.dp else 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(if (isMobile) 16.dp else 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showFormatGuide = true }) {
                            Icon(Icons.Default.Info, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            AppText("View CSV Format Guide", style = MaterialTheme.typography.labelMedium)
                        }
                    }

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

                    UserTable(
                        users = uiState.users,
                        totalCount = uiState.totalUsers,
                        currentPage = uiState.currentPage,
                        rowsPerPage = uiState.rowsPerPage,
                        onPageChange = viewModel::onPageChanged,
                        onRowsPerPageChange = viewModel::onRowsPerPageChanged,
                        onUpdateStatus = viewModel::updateUserStatus,
                        onEdit = viewModel::setEditingUser,
                        onDelete = viewModel::deleteUser,
                        canEdit = uiState.canEdit
                    )
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
    BoxWithConstraints {
        val isMobile = maxWidth < 600.dp
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surfaceContainerLowest
        ) {
            if (isMobile) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { AppText("Search users...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        singleLine = true
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var roleMenuExpanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.weight(1f)) {
                            FilterDropdown(
                                label = "Role: ${roles.find { it.id == selectedRoleId }?.roleName ?: "All"}",
                                onClick = { roleMenuExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(expanded = roleMenuExpanded, onDismissRequest = { roleMenuExpanded = false }) {
                                DropdownMenuItem(text = { Text("All Roles") }, onClick = { onRoleChange(null); roleMenuExpanded = false })
                                roles.forEach { role ->
                                    DropdownMenuItem(text = { Text(role.roleName) }, onClick = { onRoleChange(role.id); roleMenuExpanded = false })
                                }
                            }
                        }

                        var statusMenuExpanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.weight(1f)) {
                            FilterDropdown(
                                label = "Status: ${selectedStatus?.name ?: "All"}",
                                onClick = { statusMenuExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(expanded = statusMenuExpanded, onDismissRequest = { statusMenuExpanded = false }) {
                                DropdownMenuItem(text = { Text("All Status") }, onClick = { onStatusChange(null); statusMenuExpanded = false })
                                UserStatus.entries.forEach { status ->
                                    DropdownMenuItem(text = { Text(status.name) }, onClick = { onStatusChange(status); statusMenuExpanded = false })
                                }
                            }
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onSearchChange(""); onRoleChange(null); onStatusChange(null) },
                            modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.Refresh, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        
                        TextButton(onClick = {}) {
                            Icon(Icons.Default.Download, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            AppText("Export CSV", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
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
    }
}

@Composable
private fun FilterDropdown(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
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
            AppText(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
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
    onUpdateStatus: (UserUiModel, UserStatus) -> Unit,
    onEdit: (UserUiModel) -> Unit,
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
                val isMobile = maxWidth < 800.dp
                if (isMobile) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        users.forEach { user ->
                            UserMobileCard(user, onUpdateStatus, onEdit, onDelete, canEdit)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                        val minTableWidth = 900.dp
                        val tableWidth = if (viewportWidth > minTableWidth) viewportWidth else minTableWidth
                        
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
                                Spacer(Modifier.width(150.dp)) // Match row actions width
                            }
                            
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            
                            users.forEach { user ->
                                UserTableRow(user, onUpdateStatus, onEdit, onDelete, canEdit)
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
    onUpdateStatus: (UserUiModel, UserStatus) -> Unit,
    onEdit: (UserUiModel) -> Unit,
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
                    AppText(user.phone, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                IconButton(onClick = { onEdit(user) }) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }

                var showStatusMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { showStatusMenu = true }) {
                        Icon(Icons.Default.MoreVert, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    DropdownMenu(expanded = showStatusMenu, onDismissRequest = { showStatusMenu = false }) {
                        UserStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text("Set as ${status.name}") },
                                onClick = {
                                    onUpdateStatus(user, status)
                                    showStatusMenu = false
                                },
                                leadingIcon = {
                                    val icon = if (status == UserStatus.ACTIVE) Icons.Default.CheckCircle else Icons.Default.Cancel
                                    val color = if (status == UserStatus.ACTIVE) AcademicSuccess else AcademicError
                                    Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                                }
                            )
                        }
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Delete User", color = AcademicError) },
                            onClick = { onDelete(user.id); showStatusMenu = false },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = AcademicError, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }
            } else {
                IconButton(onClick = {}) { Icon(Icons.Default.Visibility, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@Composable
private fun UserTableRow(
    user: UserUiModel,
    onUpdateStatus: (UserUiModel, UserStatus) -> Unit,
    onEdit: (UserUiModel) -> Unit,
    onDelete: (Long) -> Unit,
    canEdit: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
                AppText(user.phone, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

        Row(modifier = Modifier.width(150.dp), horizontalArrangement = Arrangement.End) {
            if (canEdit) {
                IconButton(onClick = { onEdit(user) }) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }

                var showStatusMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { showStatusMenu = true }) {
                        Icon(Icons.Default.MoreVert, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    DropdownMenu(expanded = showStatusMenu, onDismissRequest = { showStatusMenu = false }) {
                        UserStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text("Set as ${status.name}") },
                                onClick = {
                                    onUpdateStatus(user, status)
                                    showStatusMenu = false
                                },
                                leadingIcon = {
                                    val icon = if (status == UserStatus.ACTIVE) Icons.Default.CheckCircle else Icons.Default.Cancel
                                    val color = if (status == UserStatus.ACTIVE) AcademicSuccess else AcademicError
                                    Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                                }
                            )
                        }
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Delete User", color = AcademicError) },
                            onClick = { onDelete(user.id); showStatusMenu = false },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = AcademicError, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }
            } else {
                IconButton(onClick = { /* View Details Logic */ }) { Icon(Icons.Default.Visibility, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@Composable
private fun EditUserDialog(
    user: UserUiModel,
    roles: List<com.kastack.vidyanet.models.role.RoleDto>,
    onDismiss: () -> Unit,
    onSave: (name: String, email: String, roleIds: List<Long>) -> Unit
) {
    var name by remember(user.id) { mutableStateOf(user.name) }
    var email by remember(user.id) { mutableStateOf(user.email) }
    val selectedRoleIds = remember(user.id) { mutableStateListOf<Long>() }

    LaunchedEffect(user.id, user.roleIds) {
        selectedRoleIds.clear()
        selectedRoleIds.addAll(user.roleIds)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { AppText("Edit User", fontWeight = FontWeight.Bold) },
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
                    schema = ValidationSchema.user.email,
                    value = email,
                    onValueChange = { email = it },
                )
                
                AppText("Assign Roles (Multiple Allowed)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                roles.forEach { role ->
                    val isSelected = selectedRoleIds.contains(role.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                if (isSelected) selectedRoleIds.remove(role.id)
                                else selectedRoleIds.add(role.id)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null
                        )
                        AppText(role.roleName)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank() && selectedRoleIds.isNotEmpty()) onSave(name, email, selectedRoleIds.toList()) }) {
                AppText("Save Changes")
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
private fun AddUserDialog(
    roles: List<com.kastack.vidyanet.models.role.RoleDto>,
    onDismiss: () -> Unit,
    onAdd: (name: String, phone: String, email: String, roleIds: List<Long>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val selectedRoleIds = remember { mutableStateListOf<Long>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { AppText("Add User", fontWeight = FontWeight.Bold) },
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
                    val isSelected = selectedRoleIds.contains(role.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                if (isSelected) selectedRoleIds.remove(role.id)
                                else selectedRoleIds.add(role.id)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null
                        )
                        AppText(role.roleName)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank() && phone.isNotBlank() && selectedRoleIds.isNotEmpty()) onAdd(name, phone, email, selectedRoleIds.toList()) }) {
                AppText("Add User")
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
private fun CsvFormatGuideDialog(
    roles: List<com.kastack.vidyanet.models.role.RoleDto>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { AppText("CSV Import Format Guide", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppText("Your CSV file must include the following headers (case-insensitive):", style = MaterialTheme.typography.bodyMedium)
                
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        AppText("Full Name, Phone, Email, Roles", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                AppText("Available Roles & IDs:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        roles.forEach { role ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                AppText(role.roleName, style = MaterialTheme.typography.bodySmall)
                                AppText("ID: ${role.id}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                AppText("Notes:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    AppText("• Full Name and Phone are required.", style = MaterialTheme.typography.bodySmall)
                    AppText("• Roles should be a list of numeric IDs (from the list above) separated by semicolon (;).", style = MaterialTheme.typography.bodySmall)
                }

                AppText("Example Row:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val exampleId = roles.firstOrNull()?.id ?: 1
                    AppText(
                        "John Doe, +919876543210, john@example.com, $exampleId",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                AppText("Got it")
            }
        }
    )
}
