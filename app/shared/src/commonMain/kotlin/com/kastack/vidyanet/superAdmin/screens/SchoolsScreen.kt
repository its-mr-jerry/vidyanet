package com.kastack.vidyanet.superAdmin.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kastack.vidyanet.commonUi.components.AppPagination
import com.kastack.vidyanet.commonUi.components.AppText
import com.kastack.vidyanet.constants.IndiaConstants
import com.kastack.vidyanet.models.schoolUser.SchoolStatus
import com.kastack.vidyanet.superAdmin.components.AddSchoolDialog
import com.kastack.vidyanet.superAdmin.components.EditSchoolDialog
import com.kastack.vidyanet.superAdmin.components.SuperAdminLayout
import com.kastack.vidyanet.superAdmin.viewModels.SchoolDetail
import com.kastack.vidyanet.superAdmin.viewModels.SchoolsViewModel
import com.kastack.vidyanet.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SchoolsScreen(
    onNavigate: (String) -> Unit,
    onSchoolClick: (String) -> Unit,
    viewModel: SchoolsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    if (uiState.isAddSchoolDialogVisible) {
        AddSchoolDialog(
            onDismiss = viewModel::hideAddSchoolDialog,
            onConfirm = viewModel::createSchool,
            isLoading = uiState.isCreatingSchool
        )
    }

    if (uiState.isEditSchoolDialogVisible && uiState.editingSchool != null) {
        EditSchoolDialog(
            school = uiState.editingSchool!!,
            onDismiss = viewModel::hideEditSchoolDialog,
            onConfirm = { request -> viewModel.updateSchool(uiState.editingSchool!!.id.toString(), request) },
            isLoading = uiState.isLoading
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        SuperAdminLayout(
            currentDestination = "Schools",
            onNavigate = onNavigate
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                // Header Section
                HeaderSection(
                    searchQuery = uiState.searchQuery,
                    onSearchChange = viewModel::onSearchQueryChanged,
                    onAddClick = viewModel::showAddSchoolDialog
                )

                // Filters Section
                FiltersSection(
                    statusFilter = uiState.statusFilter,
                    onStatusChange = viewModel::onStatusFilterChanged,
                    stateFilter = uiState.stateFilter,
                    onStateChange = viewModel::onStateFilterChanged,
                    boardFilter = uiState.boardFilter,
                    onBoardChange = viewModel::onBoardFilterChanged,
                    planFilter = uiState.planFilter,
                    onPlanChange = viewModel::onPlanFilterChanged,
                    sortOption = uiState.sortOption,
                    onSortChange = viewModel::onSortOptionChanged,
                    onClear = viewModel::clearFilters
                )

                // Table Section
                if (uiState.isLoading) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.error != null && uiState.schools.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AppText(uiState.error!!, color = MaterialTheme.colorScheme.error)
                            Button(onClick = viewModel::loadSchools) {
                                AppText("Retry")
                            }
                        }
                    }
                } else {
                    SchoolsTable(
                        schools = uiState.schools,
                        onUpdateStatus = viewModel::updateSchoolStatus,
                        onEdit = viewModel::showEditSchoolDialog,
                        onDelete = viewModel::deleteSchool,
                        onSchoolClick = onSchoolClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Pagination Section
                AppPagination(
                    totalItems = uiState.totalSchools,
                    currentPage = uiState.currentPage,
                    rowsPerPage = uiState.rowsPerPage,
                    onPageChange = viewModel::onPageChanged,
                    onRowsPerPageChange = viewModel::onRowsPerPageChanged
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppText("Dashboard", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    AppText(" / ", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    AppText("Schools", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(modifier = Modifier.height(8.dp))
                AppText("Institutional Directory", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { AppText("Search schools, principals, or codes...", style = MaterialTheme.typography.bodyMedium) },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.width(360.dp).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                Button(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(48.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    AppText("Add New School", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun FiltersSection(
    statusFilter: String,
    onStatusChange: (String) -> Unit,
    stateFilter: String,
    onStateChange: (String) -> Unit,
    boardFilter: String,
    onBoardChange: (String) -> Unit,
    planFilter: String,
    onPlanChange: (String) -> Unit,
    sortOption: String,
    onSortChange: (String) -> Unit,
    onClear: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterDropdown("Filter by Status", statusFilter, listOf("All Statuses", "Active", "Pending", "Inactive"), onStatusChange, Modifier.weight(1f))
            FilterDropdown("State", stateFilter, listOf("All States") + IndiaConstants.states, onStateChange, Modifier.weight(1f))
            FilterDropdown("Board", boardFilter, listOf("All Boards", "CBSE", "ICSE", "IB", "State Board"), onBoardChange, Modifier.weight(1f))
            FilterDropdown("Subscription Plan", planFilter, listOf("All Plans", "Enterprise", "Professional", "Standard", "Free Trial"), onPlanChange, Modifier.weight(1f))
            FilterDropdown("Sort By", sortOption, listOf("None", "Students (High to Low)", "Students (Low to High)", "Name (A-Z)"), onSortChange, Modifier.weight(1f))
            
            Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.height(60.dp)) {
                TextButton(onClick = onClear) {
                    Icon(Icons.Default.FilterListOff, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    AppText("Clear")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        AppText(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { AppText(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SchoolsTable(
    schools: List<SchoolDetail>,
    onUpdateStatus: (String, SchoolStatus) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    onSchoolClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            // Table Header
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLow).padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableHeaderItem("School Name & Code", Modifier.weight(2.5f))
                TableHeaderItem("Principal", Modifier.weight(1.5f))
                TableHeaderItem("Students", Modifier.weight(1f))
                TableHeaderItem("Teachers", Modifier.weight(1f))
                TableHeaderItem("Status", Modifier.weight(1.2f))
                TableHeaderItem("Plan", Modifier.weight(1.2f))
                TableHeaderItem("Actions", Modifier.weight(1f), TextAlign.End)
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(schools) { school ->
                    SchoolRow(school, onUpdateStatus, onEdit, onDelete, onSchoolClick)
                }
            }
        }
    }
}

@Composable
private fun TableHeaderItem(text: String, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.Start) {
    AppText(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = textAlign
    )
}

@Composable
private fun SchoolRow(
    school: SchoolDetail,
    onUpdateStatus: (String, SchoolStatus) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    onSchoolClick: (String) -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Name & Code
        Row(
            modifier = Modifier
                .weight(2.5f)
                .clickable { onSchoolClick(school.id) }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                if (school.logoUrl != null) {
                    AsyncImage(
                        model = school.logoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.School, null, modifier = Modifier.align(Alignment.Center), tint = MaterialTheme.colorScheme.outlineVariant)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                AppText(school.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                AppText(school.code, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        AppText(school.principal, Modifier.weight(1.5f), style = MaterialTheme.typography.bodyMedium)
        AppText(school.students.toString(), Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        AppText(school.teachers.toString(), Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)

        // Status
        Box(modifier = Modifier.weight(1.2f)) {
            val (bgColor, textColor) = when(school.status) {
                "ACTIVE" -> Color(0xFFDCFCE7) to AcademicSuccess
                "PENDING_APPROVAL" -> Color(0xFFFEF3C7) to AcademicWarning
                else -> Color(0xFFFEE2E2) to AcademicError
            }
            Row(
                modifier = Modifier
                    .background(bgColor, CircleShape)
                    .clickable { showStatusMenu = true }
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(6.dp).background(textColor, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                AppText(school.status, style = MaterialTheme.typography.labelSmall, color = textColor, fontWeight = FontWeight.Bold)
            }

            DropdownMenu(
                expanded = showStatusMenu,
                onDismissRequest = { showStatusMenu = false }
            ) {
                SchoolStatus.entries.forEach { status ->
                    DropdownMenuItem(
                        text = { AppText(status.name) },
                        onClick = {
                            onUpdateStatus(school.id, status)
                            showStatusMenu = false
                        }
                    )
                }
            }
        }

        // Plan
        Row(modifier = Modifier.weight(1.2f), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (school.plan == "Enterprise") Icons.Default.WorkspacePremium else Icons.Outlined.VerifiedUser,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (school.plan == "Enterprise") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            AppText(school.plan, style = MaterialTheme.typography.bodyMedium, color = if (school.plan == "Enterprise") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
        }

        // Actions
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { /* View Detail */ }) { Icon(Icons.Outlined.Visibility, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            IconButton(onClick = { onEdit(school.id) }) { Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            IconButton(onClick = { onDelete(school.id) }) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(20.dp), tint = AcademicError) }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
}
