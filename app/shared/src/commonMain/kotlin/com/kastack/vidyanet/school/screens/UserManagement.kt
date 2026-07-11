package com.kastack.vidyanet.school.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kastack.vidyanet.commonUi.components.AppPagination
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
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserManagement(
    viewModel: UserManagementViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Navigation / Header
        SchoolSettingsHeader(
            title = "User Management",
            subtitle = "Manage staff, teacher, and administrator accounts and access levels.",
            breadcrumbs = listOf("Settings", "User Management"),
            primaryAction = HeaderAction(
                label = "Invite User",
                icon = Icons.Default.Add,
                onClick = { /* Invite logic */ }
            ),
            secondaryAction = HeaderAction(
                label = "Bulk Invite",
                icon = Icons.Default.UploadFile,
                onClick = { /* Bulk logic */ }
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Filter Bar
            FilterBar()

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
                                onRowsPerPageChange = viewModel::onRowsPerPageChanged
                            )
                        }

                        // Right Sidebar Content
                        Column(modifier = Modifier.weight(0.35f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            PendingInvitesSection(uiState.pendingInvites)
                            RolesSummaryCard()
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
                            onRowsPerPageChange = viewModel::onRowsPerPageChanged
                        )
                        PendingInvitesSection(uiState.pendingInvites)
                        RolesSummaryCard()
                    }
                }
            }
        }
    }
}


@Composable
private fun FilterBar() {
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
                value = "",
                onValueChange = {},
                placeholder = { AppText("Search by name or email...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                singleLine = true
            )
            
            FilterDropdown("Role: All")
            FilterDropdown("Status: All")
            
            IconButton(
                onClick = {},
                modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.Tune, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
private fun FilterDropdown(label: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        onClick = {}
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
    onRowsPerPageChange: (Int) -> Unit
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
                            UserMobileCard(user)
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
                                UserTableRow(user)
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
private fun UserMobileCard(user: UserUiModel) {
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
            val roleColor = when(user.role) {
                "ADMIN" -> MaterialTheme.colorScheme.secondaryContainer
                "TEACHER" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.1f)
            }
            val roleTextColor = when(user.role) {
                "ADMIN" -> MaterialTheme.colorScheme.onSecondaryContainer
                "TEACHER" -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.tertiary
            }
            Surface(color = roleColor, shape = CircleShape) {
                AppText(
                    text = user.role, 
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp), 
                    style = MaterialTheme.typography.labelSmall, 
                    fontWeight = FontWeight.Black, 
                    color = roleTextColor
                )
            }
            
            AppText("Last Login: ${user.lastLogin}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = {}) { Icon(Icons.Default.LockReset, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            IconButton(onClick = {}) { Icon(Icons.Default.Key, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            IconButton(onClick = {}) { Icon(if (user.status == UserStatus.ACTIVE) Icons.Default.PersonOff else Icons.Default.Check, null, modifier = Modifier.size(18.dp), tint = if (user.status == UserStatus.ACTIVE) AcademicError else MaterialTheme.colorScheme.primary) }
        }
    }
}

@Composable
private fun UserTableRow(user: UserUiModel) {
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
            val roleColor = when(user.role) {
                "ADMIN" -> MaterialTheme.colorScheme.secondaryContainer
                "TEACHER" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.1f)
            }
            val roleTextColor = when(user.role) {
                "ADMIN" -> MaterialTheme.colorScheme.onSecondaryContainer
                "TEACHER" -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.tertiary
            }
            Surface(color = roleColor, shape = CircleShape) {
                AppText(user.role, modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = roleTextColor)
            }
        }
        
        AppText(user.lastLogin, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val statusColor = if (user.status == UserStatus.ACTIVE) AcademicSuccess else MaterialTheme.colorScheme.outlineVariant
            Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
            AppText(if (user.status == UserStatus.ACTIVE) "Active" else "Inactive", style = MaterialTheme.typography.bodySmall)
        }
        
        Row(modifier = Modifier.width(100.dp), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = {}) { Icon(Icons.Default.LockReset, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            IconButton(onClick = {}) { Icon(Icons.Default.Key, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            IconButton(onClick = {}) { Icon(if (user.status == UserStatus.ACTIVE) Icons.Default.PersonOff else Icons.Default.Check, null, modifier = Modifier.size(18.dp), tint = if (user.status == UserStatus.ACTIVE) AcademicError else MaterialTheme.colorScheme.primary) }
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
private fun RolesSummaryCard() {
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
            
            SummaryItem("Administrators", 12)
            SummaryItem("Teaching Staff", 84)
            SummaryItem("Support Staff", 28)
            
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
