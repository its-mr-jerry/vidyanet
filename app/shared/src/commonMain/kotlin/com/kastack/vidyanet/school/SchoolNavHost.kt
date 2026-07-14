package com.kastack.vidyanet.school

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.kastack.vidyanet.commonUi.components.AppText
import com.kastack.vidyanet.core.GlobalStore
import com.kastack.vidyanet.permissions.PermissionSchema
import com.kastack.vidyanet.school.screens.*
import org.koin.compose.koinInject
import androidx.compose.material.icons.Icons

@Composable
fun SchoolNavHost(
    schoolId: String,
    backStack: NavBackStack<SchoolDestination>,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val globalStore: GlobalStore = koinInject()
    val currentUser by globalStore.currentUser.collectAsState()

    // Auto-redirect if the current destination is forbidden but the user has other permissions
    LaunchedEffect(currentUser, backStack.last()) {
        if (currentUser != null) {
            val current = backStack.last()
            if (!checkModulePermission(current, globalStore)) {
                val firstAllowed = getFirstPermittedDestination(globalStore)
                if (firstAllowed != null && firstAllowed != current) {
                    backStack.clear()
                    backStack.add(firstAllowed)
                }
            }
        }
    }

    val currentDest = backStack.last()
    val isAuthorized = checkModulePermission(currentDest, globalStore)
    val firstAllowed = getFirstPermittedDestination(globalStore)
    val hasAnyAccess = firstAllowed != null

    SchoolApp(
        schoolId = schoolId,
        currentDestination = currentDest,
        onNavigate = { dest -> 
            if (backStack.last() != dest) {
                backStack.add(dest) 
            }
        },
        onLogout = onLogout
    ) { padding ->
        if (isAuthorized) {
            NavDisplay(
                backStack = backStack,
                modifier = modifier.padding(padding)
            ) { destination ->
                NavEntry(destination) {
                    when (destination) {
                        is SchoolDestination.DashboardOverview -> SchoolDashboard(schoolId = schoolId)
                        is SchoolDestination.SettingsSchool -> SchoolSettings(schoolId = schoolId)
                        is SchoolDestination.SettingsAcademic -> AcademicSettings(schoolId = schoolId)
                        is SchoolDestination.SettingsUserManagement -> UserManagement(schoolId = schoolId)
                        is SchoolDestination.SettingsRolesPermissions -> RolesPermissions()
                        is SchoolDestination.SettingsNotifications -> NotificationSettings(schoolId = schoolId)
                        is SchoolDestination.SettingsIntegrations -> Integrations()
                        is SchoolDestination.SettingsBackupRestore -> BackupRestore()
                        is SchoolDestination.SettingsAuditLogs -> AuditLogs(schoolId = schoolId)
                        else -> PlaceholderScreen(destination.toString(), schoolId)
                    }
                }
            }
        } else if (hasAnyAccess) {
            // Waiting for redirect or showing first allowed
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        } else {
            // Only show Access Denied if they have NO permissions at all
            Box(Modifier.fillMaxSize().padding(padding)) {
                AccessDeniedScreen()
            }
        }
    }
}

@Composable
private fun AccessDeniedScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(16.dp))
            AppText(
                "Access Denied",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            AppText(
                "You do not have permission to view this module. Please contact your administrator.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

private fun checkModulePermission(destination: SchoolDestination, globalStore: GlobalStore): Boolean {
    return when (destination) {
        is SchoolDestination.DashboardOverview,
        is SchoolDestination.DashboardAnalytics,
        is SchoolDestination.DashboardNotifications,
        is SchoolDestination.DashboardCalendar,
        is SchoolDestination.DashboardQuickActions,
        is SchoolDestination.DashboardRecentActivities -> globalStore.hasPermission(PermissionSchema.Dashboard.MODULE, "VIEW")
        
        is SchoolDestination.AdmissionsEnquiries,
        is SchoolDestination.AdmissionsApplications,
        is SchoolDestination.AdmissionsDocumentVerification,
        is SchoolDestination.AdmissionsApproval,
        is SchoolDestination.AdmissionsAllocation,
        is SchoolDestination.AdmissionsWaitingList,
        is SchoolDestination.AdmissionsReports -> globalStore.hasPermission(PermissionSchema.Admissions.MODULE, "VIEW")

        is SchoolDestination.StudentsList,
        is SchoolDestination.StudentsAdd,
        is SchoolDestination.StudentsProfile,
        is SchoolDestination.StudentsDocuments,
        is SchoolDestination.StudentsPromotion,
        is SchoolDestination.StudentsTransfer,
        is SchoolDestination.StudentsAlumni,
        is SchoolDestination.StudentsReports -> globalStore.hasPermission(PermissionSchema.Students.MODULE, "VIEW")

        is SchoolDestination.ParentsList,
        is SchoolDestination.ParentsGuardians,
        is SchoolDestination.ParentsPortalAccess,
        is SchoolDestination.ParentsCommunication,
        is SchoolDestination.ParentsReports -> globalStore.hasPermission(PermissionSchema.Parents.MODULE, "VIEW")

        is SchoolDestination.StaffEmployees,
        is SchoolDestination.StaffTeachers,
        is SchoolDestination.StaffDepartments,
        is SchoolDestination.StaffDesignations,
        is SchoolDestination.StaffLeave,
        is SchoolDestination.StaffAttendance,
        is SchoolDestination.StaffPayroll,
        is SchoolDestination.StaffPerformance,
        is SchoolDestination.StaffRecruitment,
        is SchoolDestination.StaffDocuments,
        is SchoolDestination.StaffReports -> globalStore.hasPermission(PermissionSchema.Staff.MODULE, "VIEW")

        is SchoolDestination.AcademicsSessions,
        is SchoolDestination.AcademicsTerms,
        is SchoolDestination.AcademicsClasses,
        is SchoolDestination.AcademicsSections,
        is SchoolDestination.AcademicsSubjects,
        is SchoolDestination.AcademicsSubjectGroups,
        is SchoolDestination.AcademicsClassTeacherAllocation,
        is SchoolDestination.AcademicsSubjectTeacherAllocation,
        is SchoolDestination.AcademicsCurriculum,
        is SchoolDestination.AcademicsCalendar -> globalStore.hasPermission(PermissionSchema.Academics.MODULE, "VIEW")

        is SchoolDestination.AttendanceStudent,
        is SchoolDestination.AttendanceStaff,
        is SchoolDestination.AttendanceBiometric,
        is SchoolDestination.AttendanceReports,
        is SchoolDestination.AttendanceSettings -> globalStore.hasPermission(PermissionSchema.Attendance.MODULE, "VIEW")

        is SchoolDestination.TimetableClass,
        is SchoolDestination.TimetableTeacher,
        is SchoolDestination.TimetableRooms,
        is SchoolDestination.TimetablePeriods,
        is SchoolDestination.TimetableSettings -> globalStore.hasPermission(PermissionSchema.Timetable.MODULE, "VIEW")

        is SchoolDestination.ExamTypes,
        is SchoolDestination.ExamSchedule,
        is SchoolDestination.ExamMarksEntry,
        is SchoolDestination.ExamGradeSystem,
        is SchoolDestination.ExamResultProcessing,
        is SchoolDestination.ExamReportCards,
        is SchoolDestination.ExamPromotion,
        is SchoolDestination.ExamReports -> globalStore.hasPermission(PermissionSchema.Examinations.MODULE, "VIEW")

        is SchoolDestination.FinanceFeesStructures,
        is SchoolDestination.FinanceFeesCategories,
        is SchoolDestination.FinanceFeesDiscounts,
        is SchoolDestination.FinanceFeesScholarships,
        is SchoolDestination.FinanceFeesCollection,
        is SchoolDestination.FinanceFeesDuePayments,
        is SchoolDestination.FinanceFeesReceipts,
        is SchoolDestination.FinanceFeesReports,
        is SchoolDestination.FinancePayrollSalaryStructure,
        is SchoolDestination.FinancePayrollSalaryProcessing,
        is SchoolDestination.FinancePayrollPayslips,
        is SchoolDestination.FinancePayrollReports,
        is SchoolDestination.FinanceExpensesCategories,
        is SchoolDestination.FinanceExpensesList,
        is SchoolDestination.FinanceExpensesVendors,
        is SchoolDestination.FinanceExpensesReports,
        is SchoolDestination.FinanceFinancialReports -> globalStore.hasPermission(PermissionSchema.Finance.MODULE, "VIEW")

        is SchoolDestination.LibraryBooks,
        is SchoolDestination.LibraryCategories,
        is SchoolDestination.LibraryAuthors,
        is SchoolDestination.LibraryBookIssue,
        is SchoolDestination.LibraryBookReturn,
        is SchoolDestination.LibraryFineManagement,
        is SchoolDestination.LibraryReports -> globalStore.hasPermission(PermissionSchema.Library.MODULE, "VIEW")

        is SchoolDestination.TransportVehicles,
        is SchoolDestination.TransportDrivers,
        is SchoolDestination.TransportRoutes,
        is SchoolDestination.TransportStops,
        is SchoolDestination.TransportStudentAllocation,
        is SchoolDestination.TransportTracking,
        is SchoolDestination.TransportReports -> globalStore.hasPermission(PermissionSchema.Transport.MODULE, "VIEW")

        is SchoolDestination.InventoryAssets,
        is SchoolDestination.InventoryCategories,
        is SchoolDestination.InventoryStock,
        is SchoolDestination.InventoryPurchaseOrders,
        is SchoolDestination.InventoryVendors,
        is SchoolDestination.InventoryAssetAssignment,
        is SchoolDestination.InventoryReports -> globalStore.hasPermission(PermissionSchema.Inventory.MODULE, "VIEW")

        is SchoolDestination.CommunicationAnnouncements,
        is SchoolDestination.CommunicationSMS,
        is SchoolDestination.CommunicationEmail,
        is SchoolDestination.CommunicationPushNotifications,
        is SchoolDestination.CommunicationCirculars,
        is SchoolDestination.CommunicationEvents,
        is SchoolDestination.CommunicationNoticeBoard,
        is SchoolDestination.CommunicationTemplates -> globalStore.hasPermission(PermissionSchema.Communication.MODULE, "VIEW")

        is SchoolDestination.ReportsStudent,
        is SchoolDestination.ReportsAcademic,
        is SchoolDestination.ReportsAttendance,
        is SchoolDestination.ReportsStaff,
        is SchoolDestination.ReportsFinance,
        is SchoolDestination.ReportsAdmission,
        is SchoolDestination.ReportsLibrary,
        is SchoolDestination.ReportsTransport,
        is SchoolDestination.ReportsInventory,
        is SchoolDestination.ReportsCustom -> globalStore.hasPermission(PermissionSchema.Reports.MODULE, "VIEW")

        is SchoolDestination.SettingsSchool,
        is SchoolDestination.SettingsAcademic,
        is SchoolDestination.SettingsUserManagement,
        is SchoolDestination.SettingsRolesPermissions,
        is SchoolDestination.SettingsNotifications,
        is SchoolDestination.SettingsIntegrations,
        is SchoolDestination.SettingsBackupRestore,
        is SchoolDestination.SettingsAuditLogs,
        is SchoolDestination.SettingsSchoolProfile,
        is SchoolDestination.SettingsSchoolBranches,
        is SchoolDestination.SettingsSchoolLogo,
        is SchoolDestination.SettingsSchoolContact,
        is SchoolDestination.SettingsSchoolWorkingHours,
        is SchoolDestination.SettingsAcademicYear,
        is SchoolDestination.SettingsAcademicGrading,
        is SchoolDestination.SettingsAcademicAttendance,
        is SchoolDestination.SettingsAcademicHolidays,
        is SchoolDestination.SettingsAcademicPromotion,
        is SchoolDestination.SettingsUserList,
        is SchoolDestination.SettingsUserInvite,
        is SchoolDestination.SettingsUserStatus,
        is SchoolDestination.SettingsUserPasswordReset,
        is SchoolDestination.SettingsRolesList,
        is SchoolDestination.SettingsRolesModuleAccess,
        is SchoolDestination.SettingsRolesWorkflows -> globalStore.hasPermission(PermissionSchema.Settings.MODULE, "VIEW")
    }
}

private fun getFirstPermittedDestination(globalStore: GlobalStore): SchoolDestination? {
    if (globalStore.hasPermission(PermissionSchema.Dashboard.MODULE, "VIEW")) return SchoolDestination.DashboardOverview
    if (globalStore.hasPermission(PermissionSchema.Admissions.MODULE, "VIEW")) return SchoolDestination.AdmissionsEnquiries
    if (globalStore.hasPermission(PermissionSchema.Students.MODULE, "VIEW")) return SchoolDestination.StudentsList
    if (globalStore.hasPermission(PermissionSchema.Parents.MODULE, "VIEW")) return SchoolDestination.ParentsList
    if (globalStore.hasPermission(PermissionSchema.Staff.MODULE, "VIEW")) return SchoolDestination.StaffEmployees
    if (globalStore.hasPermission(PermissionSchema.Academics.MODULE, "VIEW")) return SchoolDestination.AcademicsSessions
    if (globalStore.hasPermission(PermissionSchema.Attendance.MODULE, "VIEW")) return SchoolDestination.AttendanceStudent
    if (globalStore.hasPermission(PermissionSchema.Timetable.MODULE, "VIEW")) return SchoolDestination.TimetableClass
    if (globalStore.hasPermission(PermissionSchema.Examinations.MODULE, "VIEW")) return SchoolDestination.ExamTypes
    if (globalStore.hasPermission(PermissionSchema.Finance.MODULE, "VIEW")) return SchoolDestination.FinanceFeesStructures
    if (globalStore.hasPermission(PermissionSchema.Library.MODULE, "VIEW")) return SchoolDestination.LibraryBooks
    if (globalStore.hasPermission(PermissionSchema.Transport.MODULE, "VIEW")) return SchoolDestination.TransportVehicles
    if (globalStore.hasPermission(PermissionSchema.Inventory.MODULE, "VIEW")) return SchoolDestination.InventoryAssets
    if (globalStore.hasPermission(PermissionSchema.Communication.MODULE, "VIEW")) return SchoolDestination.CommunicationAnnouncements
    if (globalStore.hasPermission(PermissionSchema.Reports.MODULE, "VIEW")) return SchoolDestination.ReportsStudent
    if (globalStore.hasPermission(PermissionSchema.Settings.MODULE, "VIEW")) return SchoolDestination.SettingsSchool
    return null
}

@Composable
private fun PlaceholderScreen(title: String, schoolId: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AppText(title.replace("([a-z])([A-Z])".toRegex(), "$1 $2"), style = MaterialTheme.typography.headlineLarge)
            AppText("School: $schoolId", style = MaterialTheme.typography.bodyLarge)
            AppText("This module is coming soon.", color = MaterialTheme.colorScheme.outline)
        }
    }
}
