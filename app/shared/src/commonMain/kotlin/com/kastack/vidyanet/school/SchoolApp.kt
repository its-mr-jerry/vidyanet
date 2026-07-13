package com.kastack.vidyanet.school

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kastack.vidyanet.commonUi.components.AppText
import com.kastack.vidyanet.school.viewModels.SchoolAppViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.launch

@Composable
fun SchoolApp(
    schoolId: String,
    currentDestination: SchoolDestination,
    onNavigate: (SchoolDestination) -> Unit,
    viewModel: SchoolAppViewModel = koinViewModel(),
    content: @Composable (PaddingValues) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val schoolName = uiState.school?.schoolName ?: "VidyaNet"
    
    LaunchedEffect(schoolId) {
        viewModel.loadSchoolInfo(schoolId)
    }

    var isSidebarCollapsed by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        val isMobile = maxWidth < 800.dp

        if (isMobile) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = MaterialTheme.colorScheme.primary,
                        drawerContentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.width(280.dp)
                    ) {
                        SidebarContent(
                            isCollapsed = false,
                            currentDestination = currentDestination,
                            onNavigate = {
                                onNavigate(it)
                                scope.launch { drawerState.close() }
                            },
                            schoolName = schoolName,
                            uiState = uiState
                        )
                    }
                }
            ) {
                MainContent(
                    schoolName = schoolName,
                    isMobile = true,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    uiState = uiState,
                    content = content
                )
            }
        } else {
            val sidebarWidth by animateDpAsState(targetValue = if (isSidebarCollapsed) 80.dp else 280.dp)
            Row(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier.width(sidebarWidth).fillMaxHeight(),
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    SidebarContent(
                        isCollapsed = isSidebarCollapsed,
                        currentDestination = currentDestination,
                        onNavigate = onNavigate,
                        schoolName = schoolName,
                        uiState = uiState
                    )
                }

                MainContent(
                    schoolName = schoolName,
                    isMobile = false,
                    onMenuClick = { isSidebarCollapsed = !isSidebarCollapsed },
                    uiState = uiState,
                    content = content
                )
            }
        }
    }
}

@Composable
private fun MainContent(
    schoolName: String,
    isMobile: Boolean,
    onMenuClick: () -> Unit,
    uiState: com.kastack.vidyanet.school.viewModels.SchoolAppUiState,
    content: @Composable (PaddingValues) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopHeader(
            schoolName = schoolName,
            isMobile = isMobile,
            onToggleSidebar = onMenuClick,
            uiState = uiState
        )
        Box(modifier = Modifier.weight(1f)) {
            content(PaddingValues(0.dp))
        }
        Footer(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun SidebarContent(
    isCollapsed: Boolean,
    currentDestination: SchoolDestination,
    onNavigate: (SchoolDestination) -> Unit,
    schoolName: String,
    uiState: com.kastack.vidyanet.school.viewModels.SchoolAppUiState
) {
    var expandedCategory by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(vertical = 24.dp)) {
        // School Logo & Name
        Row(
            modifier = Modifier.padding(horizontal = if (isCollapsed) 0.dp else 24.dp).fillMaxWidth(),
            horizontalArrangement = if (isCollapsed) Arrangement.Center else Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.school?.logoUrl != null) {
                    // In a real app, use an image loader here
                    Icon(Icons.Default.School, null, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.School, null, modifier = Modifier.size(24.dp))
                }
            }
            if (!isCollapsed) {
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    AppText(
                        text = "VidyaNet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    AppText(
                        text = schoolName,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.alpha(0.7f),
                        maxLines = 1,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Navigation Items
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Dashboard
            ExpandableSidebarItem(
                label = "Dashboard",
                icon = Icons.Default.Dashboard,
                isCollapsed = isCollapsed,
                items = listOf(
                    "Overview" to SchoolDestination.DashboardOverview,
                    "Analytics" to SchoolDestination.DashboardAnalytics,
                    "Notifications" to SchoolDestination.DashboardNotifications,
                    "Calendar" to SchoolDestination.DashboardCalendar,
                    "Quick Actions" to SchoolDestination.DashboardQuickActions,
                    "Activities" to SchoolDestination.DashboardRecentActivities
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Dashboard",
                onToggle = { expandedCategory = if (expandedCategory == "Dashboard") null else "Dashboard" },
                onNavigate = onNavigate
            )

            // Admissions
            ExpandableSidebarItem(
                label = "Admissions",
                icon = Icons.Default.HowToReg,
                isCollapsed = isCollapsed,
                items = listOf(
                    "Enquiries" to SchoolDestination.AdmissionsEnquiries,
                    "Applications" to SchoolDestination.AdmissionsApplications,
                    "Verification" to SchoolDestination.AdmissionsDocumentVerification,
                    "Approval" to SchoolDestination.AdmissionsApproval,
                    "Allocation" to SchoolDestination.AdmissionsAllocation,
                    "Waiting List" to SchoolDestination.AdmissionsWaitingList,
                    "Reports" to SchoolDestination.AdmissionsReports
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Admissions",
                onToggle = { expandedCategory = if (expandedCategory == "Admissions") null else "Admissions" },
                onNavigate = onNavigate
            )

            // Students
            ExpandableSidebarItem(
                label = "Students",
                icon = Icons.Default.Group,
                isCollapsed = isCollapsed,
                items = listOf(
                    "Student List" to SchoolDestination.StudentsList,
                    "Add Student" to SchoolDestination.StudentsAdd,
                    "Profile" to SchoolDestination.StudentsProfile,
                    "Documents" to SchoolDestination.StudentsDocuments,
                    "Promotion" to SchoolDestination.StudentsPromotion,
                    "Transfer" to SchoolDestination.StudentsTransfer,
                    "Alumni" to SchoolDestination.StudentsAlumni,
                    "Reports" to SchoolDestination.StudentsReports
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Students",
                onToggle = { expandedCategory = if (expandedCategory == "Students") null else "Students" },
                onNavigate = onNavigate
            )

            // Parents
            ExpandableSidebarItem(
                label = "Parents",
                icon = Icons.Default.FamilyRestroom,
                isCollapsed = isCollapsed,
                items = listOf(
                    "Parent List" to SchoolDestination.ParentsList,
                    "Guardians" to SchoolDestination.ParentsGuardians,
                    "Portal Access" to SchoolDestination.ParentsPortalAccess,
                    "Communication" to SchoolDestination.ParentsCommunication,
                    "Reports" to SchoolDestination.ParentsReports
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Parents",
                onToggle = { expandedCategory = if (expandedCategory == "Parents") null else "Parents" },
                onNavigate = onNavigate
            )

            // Staff
            ExpandableSidebarItem(
                label = "Staff",
                icon = Icons.Default.Badge,
                isCollapsed = isCollapsed,
                items = listOf(
                    "--- EMPLOYEES ---" to null,
                    "Employees" to SchoolDestination.StaffEmployees,
                    "Teachers" to SchoolDestination.StaffTeachers,
                    "--- STRUCTURE ---" to null,
                    "Departments" to SchoolDestination.StaffDepartments,
                    "Designations" to SchoolDestination.StaffDesignations,
                    "--- OPERATIONS ---" to null,
                    "Leave" to SchoolDestination.StaffLeave,
                    "Attendance" to SchoolDestination.StaffAttendance,
                    "Payroll" to SchoolDestination.StaffPayroll,
                    "Performance" to SchoolDestination.StaffPerformance,
                    "Recruitment" to SchoolDestination.StaffRecruitment,
                    "--- OTHER ---" to null,
                    "Documents" to SchoolDestination.StaffDocuments,
                    "Reports" to SchoolDestination.StaffReports
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Staff",
                onToggle = { expandedCategory = if (expandedCategory == "Staff") null else "Staff" },
                onNavigate = onNavigate
            )

            // Academics
            ExpandableSidebarItem(
                label = "Academics",
                icon = Icons.Default.AutoStories,
                isCollapsed = isCollapsed,
                items = listOf(
                    "Sessions" to SchoolDestination.AcademicsSessions,
                    "Terms" to SchoolDestination.AcademicsTerms,
                    "Classes" to SchoolDestination.AcademicsClasses,
                    "Sections" to SchoolDestination.AcademicsSections,
                    "Subjects" to SchoolDestination.AcademicsSubjects,
                    "Subject Groups" to SchoolDestination.AcademicsSubjectGroups,
                    "Class Teacher" to SchoolDestination.AcademicsClassTeacherAllocation,
                    "Subject Teacher" to SchoolDestination.AcademicsSubjectTeacherAllocation,
                    "Curriculum" to SchoolDestination.AcademicsCurriculum,
                    "Calendar" to SchoolDestination.AcademicsCalendar
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Academics",
                onToggle = { expandedCategory = if (expandedCategory == "Academics") null else "Academics" },
                onNavigate = onNavigate
            )

            // Attendance
            ExpandableSidebarItem(
                label = "Attendance",
                icon = Icons.AutoMirrored.Filled.FactCheck,
                isCollapsed = isCollapsed,
                items = listOf(
                    "Student Attendance" to SchoolDestination.AttendanceStudent,
                    "Staff Attendance" to SchoolDestination.AttendanceStaff,
                    "Biometric" to SchoolDestination.AttendanceBiometric,
                    "Reports" to SchoolDestination.AttendanceReports,
                    "Settings" to SchoolDestination.AttendanceSettings
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Attendance",
                onToggle = { expandedCategory = if (expandedCategory == "Attendance") null else "Attendance" },
                onNavigate = onNavigate
            )

            // Timetable
            ExpandableSidebarItem(
                label = "Timetable",
                icon = Icons.AutoMirrored.Filled.EventNote,
                isCollapsed = isCollapsed,
                items = listOf(
                    "Class Timetable" to SchoolDestination.TimetableClass,
                    "Teacher Timetable" to SchoolDestination.TimetableTeacher,
                    "Rooms" to SchoolDestination.TimetableRooms,
                    "Periods" to SchoolDestination.TimetablePeriods,
                    "Settings" to SchoolDestination.TimetableSettings
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Timetable",
                onToggle = { expandedCategory = if (expandedCategory == "Timetable") null else "Timetable" },
                onNavigate = onNavigate
            )

            // Examinations
            ExpandableSidebarItem(
                label = "Examinations",
                icon = Icons.Default.Quiz,
                isCollapsed = isCollapsed,
                items = listOf(
                    "Exam Types" to SchoolDestination.ExamTypes,
                    "Schedule" to SchoolDestination.ExamSchedule,
                    "Marks Entry" to SchoolDestination.ExamMarksEntry,
                    "Grading" to SchoolDestination.ExamGradeSystem,
                    "Results" to SchoolDestination.ExamResultProcessing,
                    "Report Cards" to SchoolDestination.ExamReportCards,
                    "Promotion" to SchoolDestination.ExamPromotion,
                    "Reports" to SchoolDestination.ExamReports
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Examinations",
                onToggle = { expandedCategory = if (expandedCategory == "Examinations") null else "Examinations" },
                onNavigate = onNavigate
            )

            // Finance
            ExpandableSidebarItem(
                label = "Finance",
                icon = Icons.Default.Payments,
                isCollapsed = isCollapsed,
                items = listOf(
                    "--- FEES ---" to null,
                    "Fee Structures" to SchoolDestination.FinanceFeesStructures,
                    "Fee Categories" to SchoolDestination.FinanceFeesCategories,
                    "Discounts" to SchoolDestination.FinanceFeesDiscounts,
                    "Scholarships" to SchoolDestination.FinanceFeesScholarships,
                    "Fee Collection" to SchoolDestination.FinanceFeesCollection,
                    "Due Payments" to SchoolDestination.FinanceFeesDuePayments,
                    "Receipts" to SchoolDestination.FinanceFeesReceipts,
                    "Fee Reports" to SchoolDestination.FinanceFeesReports,
                    "--- PAYROLL ---" to null,
                    "Payroll Structures" to SchoolDestination.FinancePayrollSalaryStructure,
                    "Payroll Processing" to SchoolDestination.FinancePayrollSalaryProcessing,
                    "Payslips" to SchoolDestination.FinancePayrollPayslips,
                    "Payroll Reports" to SchoolDestination.FinancePayrollReports,
                    "--- EXPENSES ---" to null,
                    "Expense Categories" to SchoolDestination.FinanceExpensesCategories,
                    "Expenses" to SchoolDestination.FinanceExpensesList,
                    "Vendors" to SchoolDestination.FinanceExpensesVendors,
                    "Expense Reports" to SchoolDestination.FinanceExpensesReports,
                    "--- SUMMARY ---" to null,
                    "Financial Reports" to SchoolDestination.FinanceFinancialReports
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Finance",
                onToggle = { expandedCategory = if (expandedCategory == "Finance") null else "Finance" },
                onNavigate = onNavigate
            )

            // Library
            ExpandableSidebarItem(
                label = "Library",
                icon = Icons.Default.LocalLibrary,
                isCollapsed = isCollapsed,
                items = listOf(
                    "Books" to SchoolDestination.LibraryBooks,
                    "Categories" to SchoolDestination.LibraryCategories,
                    "Authors" to SchoolDestination.LibraryAuthors,
                    "Issue" to SchoolDestination.LibraryBookIssue,
                    "Return" to SchoolDestination.LibraryBookReturn,
                    "Fines" to SchoolDestination.LibraryFineManagement,
                    "Reports" to SchoolDestination.LibraryReports
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Library",
                onToggle = { expandedCategory = if (expandedCategory == "Library") null else "Library" },
                onNavigate = onNavigate
            )

            // Transport
            ExpandableSidebarItem(
                label = "Transport",
                icon = Icons.Default.DirectionsBus,
                isCollapsed = isCollapsed,
                items = listOf(
                    "Vehicles" to SchoolDestination.TransportVehicles,
                    "Drivers" to SchoolDestination.TransportDrivers,
                    "Routes" to SchoolDestination.TransportRoutes,
                    "Stops" to SchoolDestination.TransportStops,
                    "Allocation" to SchoolDestination.TransportStudentAllocation,
                    "Tracking" to SchoolDestination.TransportTracking,
                    "Reports" to SchoolDestination.TransportReports
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Transport",
                onToggle = { expandedCategory = if (expandedCategory == "Transport") null else "Transport" },
                onNavigate = onNavigate
            )

            // Inventory
            ExpandableSidebarItem(
                label = "Inventory",
                icon = Icons.Default.Inventory,
                isCollapsed = isCollapsed,
                items = listOf(
                    "Assets" to SchoolDestination.InventoryAssets,
                    "Categories" to SchoolDestination.InventoryCategories,
                    "Stock" to SchoolDestination.InventoryStock,
                    "Purchase Orders" to SchoolDestination.InventoryPurchaseOrders,
                    "Vendors" to SchoolDestination.InventoryVendors,
                    "Assignment" to SchoolDestination.InventoryAssetAssignment,
                    "Reports" to SchoolDestination.InventoryReports
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Inventory",
                onToggle = { expandedCategory = if (expandedCategory == "Inventory") null else "Inventory" },
                onNavigate = onNavigate
            )

            // Hostel
            ExpandableSidebarItem(
                label = "Hostel",
                icon = Icons.Default.Hotel,
                isCollapsed = isCollapsed,
                items = listOf(
                    "Rooms" to null,
                    "Residents" to null,
                    "Attendance" to null,
                    "Fee Tracking" to null,
                    "Maintenance" to null
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Hostel",
                onToggle = { expandedCategory = if (expandedCategory == "Hostel") null else "Hostel" },
                onNavigate = onNavigate
            )

            // Sports
            ExpandableSidebarItem(
                label = "Sports",
                icon = Icons.Default.SportsBasketball,
                isCollapsed = isCollapsed,
                items = listOf(
                    "Events" to null,
                    "Teams" to null,
                    "Equipment" to null,
                    "Coaches" to null
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Sports",
                onToggle = { expandedCategory = if (expandedCategory == "Sports") null else "Sports" },
                onNavigate = onNavigate
            )

            // Communication
            ExpandableSidebarItem(
                label = "Communication",
                icon = Icons.Default.Campaign,
                isCollapsed = isCollapsed,
                items = listOf(
                    "Announcements" to SchoolDestination.CommunicationAnnouncements,
                    "SMS" to SchoolDestination.CommunicationSMS,
                    "Email" to SchoolDestination.CommunicationEmail,
                    "Push" to SchoolDestination.CommunicationPushNotifications,
                    "Circulars" to SchoolDestination.CommunicationCirculars,
                    "Events" to SchoolDestination.CommunicationEvents,
                    "Notice Board" to SchoolDestination.CommunicationNoticeBoard,
                    "Templates" to SchoolDestination.CommunicationTemplates
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Communication",
                onToggle = { expandedCategory = if (expandedCategory == "Communication") null else "Communication" },
                onNavigate = onNavigate
            )

            // Reports
            ExpandableSidebarItem(
                label = "Reports",
                icon = Icons.Default.Description,
                isCollapsed = isCollapsed,
                items = listOf(
                    "Students" to SchoolDestination.ReportsStudent,
                    "Academics" to SchoolDestination.ReportsAcademic,
                    "Attendance" to SchoolDestination.ReportsAttendance,
                    "Staff" to SchoolDestination.ReportsStaff,
                    "Finance" to SchoolDestination.ReportsFinance,
                    "Admissions" to SchoolDestination.ReportsAdmission,
                    "Library" to SchoolDestination.ReportsLibrary,
                    "Transport" to SchoolDestination.ReportsTransport,
                    "Inventory" to SchoolDestination.ReportsInventory,
                    "Custom" to SchoolDestination.ReportsCustom
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Reports",
                onToggle = { expandedCategory = if (expandedCategory == "Reports") null else "Reports" },
                onNavigate = onNavigate
            )

            // Settings
            ExpandableSidebarItem(
                label = "Settings",
                icon = Icons.Default.Settings,
                isCollapsed = isCollapsed,
                items = listOf(
                    "School Settings" to SchoolDestination.SettingsSchool,
                    "Academic Settings" to SchoolDestination.SettingsAcademic,
                    "User Management" to SchoolDestination.SettingsUserManagement,
                    "Roles & Permissions" to SchoolDestination.SettingsRolesPermissions,
                    "Notification Settings" to SchoolDestination.SettingsNotifications,
                    "Integrations" to SchoolDestination.SettingsIntegrations,
                    "Backup & Restore" to SchoolDestination.SettingsBackupRestore,
                    "Audit Logs" to SchoolDestination.SettingsAuditLogs
                ),
                currentDestination = currentDestination,
                isExpanded = expandedCategory == "Settings",
                onToggle = { expandedCategory = if (expandedCategory == "Settings") null else "Settings" },
                onNavigate = onNavigate
            )
        }

        // Bottom Profile/Logout
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))
            SidebarItem("Profile", Icons.Default.AccountCircle, false, isCollapsed, onClick = {})
            SidebarItem(
                "Logout", 
                Icons.AutoMirrored.Filled.Logout, 
                false, 
                isCollapsed, 
                onClick = {}, 
                color = MaterialTheme.colorScheme.errorContainer
            )
        }
    }
}

@Composable
private fun ExpandableSidebarItem(
    label: String,
    icon: ImageVector,
    isCollapsed: Boolean,
    items: List<Pair<String, SchoolDestination?>>,
    currentDestination: SchoolDestination,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onNavigate: (SchoolDestination) -> Unit
) {
    val isAnyChildActive = items.any { it.second == currentDestination }
    
    // Auto-expand if a child is active and nothing else is expanded
    LaunchedEffect(isAnyChildActive) {
        if (isAnyChildActive && !isExpanded) onToggle()
    }

    Column {
        Surface(
            onClick = { if (!isCollapsed) onToggle() else items.firstOrNull { it.second != null }?.second?.let { onNavigate(it) } },
            color = if (isAnyChildActive && !isExpanded) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f) else Color.Transparent,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = if (isCollapsed) 0.dp else 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isCollapsed) Arrangement.Center else Arrangement.Start
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isAnyChildActive || isExpanded) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                )
                if (!isCollapsed) {
                    Spacer(modifier = Modifier.width(12.dp))
                    AppText(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isAnyChildActive || isExpanded) FontWeight.Bold else FontWeight.Medium,
                        color = if (isAnyChildActive || isExpanded) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
                    )
                }
            }
        }
        
        if (!isCollapsed) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Box(modifier = Modifier.padding(start = 24.dp)) {
                    // Subtle Vertical Track Line
                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f))
                            .align(Alignment.CenterStart)
                    )

                    Column(
                        modifier = Modifier.padding(start = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items.forEach { (subLabel, dest) ->
                            if (dest == null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                AppText(
                                    text = subLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.alpha(0.5f),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    letterSpacing = 0.5.sp
                                )
                            } else {
                                val isActive = currentDestination == dest
                                Surface(
                                    onClick = { onNavigate(dest) },
                                    color = if (isActive) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.12f) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    AppText(
                                        text = subLabel,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                    )
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
private fun SidebarItem(
    label: String,
    icon: ImageVector,
    active: Boolean,
    isCollapsed: Boolean,
    onClick: () -> Unit,
    color: Color = Color.Unspecified
) {
    Surface(
        onClick = onClick,
        color = if (active) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (isCollapsed) 0.dp else 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isCollapsed) Arrangement.Center else Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (active) MaterialTheme.colorScheme.onPrimaryContainer else if (color != Color.Unspecified) color else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            if (!isCollapsed) {
                Spacer(modifier = Modifier.width(12.dp))
                AppText(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                    color = if (active) MaterialTheme.colorScheme.onPrimaryContainer else if (color != Color.Unspecified) color else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopHeader(
    schoolName: String,
    isMobile: Boolean,
    onToggleSidebar: () -> Unit,
    uiState: com.kastack.vidyanet.school.viewModels.SchoolAppUiState
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(64.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = if (isMobile) 12.dp else 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleSidebar) {
                Icon(Icons.Default.Menu, "Toggle Sidebar")
            }
            
            Spacer(modifier = Modifier.width(8.dp))

            AppText(
                text = schoolName,
                style = if (isMobile) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            if (!isMobile) {
                Spacer(modifier = Modifier.width(24.dp))
                
                // Global Search
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.width(8.dp))
                        AppText("Global search...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))
            }

            // Icons & Actions
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isMobile) {
                    IconButton(onClick = {}) {
                        val notificationCount = uiState.unreadNotifications
                        BadgedBox(badge = { if (notificationCount > 0) Badge { AppText(notificationCount.toString()) } }) {
                            Icon(Icons.Outlined.Notifications, null)
                        }
                    }

                    FloatingActionButton(
                        onClick = {},
                        modifier = Modifier.size(40.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(Icons.Default.Add, "Quick Create")
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Box(
                    modifier = Modifier
                        .size(if (isMobile) 32.dp else 36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    AppText(
                        text = schoolName.firstOrNull()?.toString() ?: "V",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun Footer(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(48.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AppText("VidyaNet School ERP", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                AppText("v1.0.0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextButton(onClick = {}) { AppText("Support", style = MaterialTheme.typography.labelSmall) }
            }
        }
    }
}
