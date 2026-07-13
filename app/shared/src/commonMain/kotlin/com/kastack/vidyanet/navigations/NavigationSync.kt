package com.kastack.vidyanet.navigations

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack

import com.kastack.vidyanet.superAdmin.SuperAdminDestination
import com.kastack.vidyanet.school.SchoolDestination

@Composable
expect fun BrowserHistorySync(
    mainBackStack: NavBackStack<MainDestination>,
    authBackStack: NavBackStack<AuthDestination>,
    superAdminBackStack: NavBackStack<SuperAdminDestination>,
    schoolBackStack: NavBackStack<SchoolDestination>,
    isAllowed: (NavigationState) -> Boolean
)

fun getPathForDestinations(
    main: MainDestination?,
    auth: AuthDestination?,
    superAdmin: SuperAdminDestination?,
    school: SchoolDestination?
): String {
    val path = when (main) {
        MainDestination.Auth -> when (auth) {
            AuthDestination.Splash -> "/splash"
            AuthDestination.Login -> "/login"
            null -> "/splash"
        }
        MainDestination.SuperAdmin -> when (superAdmin) {
            SuperAdminDestination.Dashboard -> "/superadmin/dashboard"
            SuperAdminDestination.Schools -> "/superadmin/schools"
            null -> "/superadmin"
        }
        is MainDestination.School -> {
            val prefix = "/school/${main.schoolId}"
            when (school) {
                // Dashboard
                SchoolDestination.DashboardOverview -> "$prefix/dashboard/overview"
                SchoolDestination.DashboardAnalytics -> "$prefix/dashboard/analytics"
                SchoolDestination.DashboardNotifications -> "$prefix/dashboard/notifications"
                SchoolDestination.DashboardCalendar -> "$prefix/dashboard/calendar"
                SchoolDestination.DashboardQuickActions -> "$prefix/dashboard/quick-actions"
                SchoolDestination.DashboardRecentActivities -> "$prefix/dashboard/activities"

                // Admissions
                SchoolDestination.AdmissionsEnquiries -> "$prefix/admissions/enquiries"
                SchoolDestination.AdmissionsApplications -> "$prefix/admissions/applications"
                SchoolDestination.AdmissionsDocumentVerification -> "$prefix/admissions/verification"
                SchoolDestination.AdmissionsApproval -> "$prefix/admissions/approval"
                SchoolDestination.AdmissionsAllocation -> "$prefix/admissions/allocation"
                SchoolDestination.AdmissionsWaitingList -> "$prefix/admissions/waiting-list"
                SchoolDestination.AdmissionsReports -> "$prefix/admissions/reports"

                // Students
                SchoolDestination.StudentsList -> "$prefix/students/list"
                SchoolDestination.StudentsAdd -> "$prefix/students/add"
                SchoolDestination.StudentsProfile -> "$prefix/students/profile"
                SchoolDestination.StudentsDocuments -> "$prefix/students/documents"
                SchoolDestination.StudentsPromotion -> "$prefix/students/promotion"
                SchoolDestination.StudentsTransfer -> "$prefix/students/transfer"
                SchoolDestination.StudentsAlumni -> "$prefix/students/alumni"
                SchoolDestination.StudentsReports -> "$prefix/students/reports"

                // Parents
                SchoolDestination.ParentsList -> "$prefix/parents/list"
                SchoolDestination.ParentsGuardians -> "$prefix/parents/guardians"
                SchoolDestination.ParentsPortalAccess -> "$prefix/parents/portal"
                SchoolDestination.ParentsCommunication -> "$prefix/parents/communication"
                SchoolDestination.ParentsReports -> "$prefix/parents/reports"

                // Staff
                SchoolDestination.StaffEmployees -> "$prefix/staff/employees"
                SchoolDestination.StaffTeachers -> "$prefix/staff/teachers"
                SchoolDestination.StaffDepartments -> "$prefix/staff/departments"
                SchoolDestination.StaffDesignations -> "$prefix/staff/designations"
                SchoolDestination.StaffLeave -> "$prefix/staff/leave"
                SchoolDestination.StaffAttendance -> "$prefix/staff/attendance"
                SchoolDestination.StaffPayroll -> "$prefix/staff/payroll"
                SchoolDestination.StaffPerformance -> "$prefix/staff/performance"
                SchoolDestination.StaffRecruitment -> "$prefix/staff/recruitment"
                SchoolDestination.StaffDocuments -> "$prefix/staff/documents"
                SchoolDestination.StaffReports -> "$prefix/staff/reports"

                // Academics
                SchoolDestination.AcademicsSessions -> "$prefix/academics/sessions"
                SchoolDestination.AcademicsTerms -> "$prefix/academics/terms"
                SchoolDestination.AcademicsClasses -> "$prefix/academics/classes"
                SchoolDestination.AcademicsSections -> "$prefix/academics/sections"
                SchoolDestination.AcademicsSubjects -> "$prefix/academics/subjects"
                SchoolDestination.AcademicsSubjectGroups -> "$prefix/academics/subject-groups"
                SchoolDestination.AcademicsClassTeacherAllocation -> "$prefix/academics/allocation-class"
                SchoolDestination.AcademicsSubjectTeacherAllocation -> "$prefix/academics/allocation-subject"
                SchoolDestination.AcademicsCurriculum -> "$prefix/academics/curriculum"
                SchoolDestination.AcademicsCalendar -> "$prefix/academics/calendar"

                // Attendance
                SchoolDestination.AttendanceStudent -> "$prefix/attendance/student"
                SchoolDestination.AttendanceStaff -> "$prefix/attendance/staff"
                SchoolDestination.AttendanceBiometric -> "$prefix/attendance/biometric"
                SchoolDestination.AttendanceReports -> "$prefix/attendance/reports"
                SchoolDestination.AttendanceSettings -> "$prefix/attendance/settings"

                // Timetable
                SchoolDestination.TimetableClass -> "$prefix/timetable/class"
                SchoolDestination.TimetableTeacher -> "$prefix/timetable/teacher"
                SchoolDestination.TimetableRooms -> "$prefix/timetable/rooms"
                SchoolDestination.TimetablePeriods -> "$prefix/timetable/periods"
                SchoolDestination.TimetableSettings -> "$prefix/timetable/settings"

                // Examinations
                SchoolDestination.ExamTypes -> "$prefix/exams/types"
                SchoolDestination.ExamSchedule -> "$prefix/exams/schedule"
                SchoolDestination.ExamMarksEntry -> "$prefix/exams/marks"
                SchoolDestination.ExamGradeSystem -> "$prefix/exams/grading"
                SchoolDestination.ExamResultProcessing -> "$prefix/exams/results"
                SchoolDestination.ExamReportCards -> "$prefix/exams/report-cards"
                SchoolDestination.ExamPromotion -> "$prefix/exams/promotion"
                SchoolDestination.ExamReports -> "$prefix/exams/reports"

                // Finance - Fees
                SchoolDestination.FinanceFeesStructures -> "$prefix/finance/fees/structures"
                SchoolDestination.FinanceFeesCategories -> "$prefix/finance/fees/categories"
                SchoolDestination.FinanceFeesDiscounts -> "$prefix/finance/fees/discounts"
                SchoolDestination.FinanceFeesScholarships -> "$prefix/finance/fees/scholarships"
                SchoolDestination.FinanceFeesCollection -> "$prefix/finance/fees/collection"
                SchoolDestination.FinanceFeesDuePayments -> "$prefix/finance/fees/due"
                SchoolDestination.FinanceFeesReceipts -> "$prefix/finance/fees/receipts"
                SchoolDestination.FinanceFeesReports -> "$prefix/finance/fees/reports"

                // Finance - Payroll
                SchoolDestination.FinancePayrollSalaryStructure -> "$prefix/finance/payroll/structures"
                SchoolDestination.FinancePayrollSalaryProcessing -> "$prefix/finance/payroll/processing"
                SchoolDestination.FinancePayrollPayslips -> "$prefix/finance/payroll/payslips"
                SchoolDestination.FinancePayrollReports -> "$prefix/finance/payroll/reports"

                // Finance - Expenses
                SchoolDestination.FinanceExpensesCategories -> "$prefix/finance/expenses/categories"
                SchoolDestination.FinanceExpensesList -> "$prefix/finance/expenses/list"
                SchoolDestination.FinanceExpensesVendors -> "$prefix/finance/expenses/vendors"
                SchoolDestination.FinanceExpensesReports -> "$prefix/finance/expenses/reports"

                SchoolDestination.FinanceFinancialReports -> "$prefix/finance/reports"

                // Library
                SchoolDestination.LibraryBooks -> "$prefix/library/books"
                SchoolDestination.LibraryCategories -> "$prefix/library/categories"
                SchoolDestination.LibraryAuthors -> "$prefix/library/authors"
                SchoolDestination.LibraryBookIssue -> "$prefix/library/issue"
                SchoolDestination.LibraryBookReturn -> "$prefix/library/return"
                SchoolDestination.LibraryFineManagement -> "$prefix/library/fines"
                SchoolDestination.LibraryReports -> "$prefix/library/reports"

                // Transport
                SchoolDestination.TransportVehicles -> "$prefix/transport/vehicles"
                SchoolDestination.TransportDrivers -> "$prefix/transport/drivers"
                SchoolDestination.TransportRoutes -> "$prefix/transport/routes"
                SchoolDestination.TransportStops -> "$prefix/transport/stops"
                SchoolDestination.TransportStudentAllocation -> "$prefix/transport/allocation"
                SchoolDestination.TransportTracking -> "$prefix/transport/tracking"
                SchoolDestination.TransportReports -> "$prefix/transport/reports"

                // Inventory
                SchoolDestination.InventoryAssets -> "$prefix/inventory/assets"
                SchoolDestination.InventoryCategories -> "$prefix/inventory/categories"
                SchoolDestination.InventoryStock -> "$prefix/inventory/stock"
                SchoolDestination.InventoryPurchaseOrders -> "$prefix/inventory/purchase-orders"
                SchoolDestination.InventoryVendors -> "$prefix/inventory/vendors"
                SchoolDestination.InventoryAssetAssignment -> "$prefix/inventory/assignment"
                SchoolDestination.InventoryReports -> "$prefix/inventory/reports"

                // Communication
                SchoolDestination.CommunicationAnnouncements -> "$prefix/communication/announcements"
                SchoolDestination.CommunicationSMS -> "$prefix/communication/sms"
                SchoolDestination.CommunicationEmail -> "$prefix/communication/email"
                SchoolDestination.CommunicationPushNotifications -> "$prefix/communication/push"
                SchoolDestination.CommunicationCirculars -> "$prefix/communication/circulars"
                SchoolDestination.CommunicationEvents -> "$prefix/communication/events"
                SchoolDestination.CommunicationNoticeBoard -> "$prefix/communication/notice-board"
                SchoolDestination.CommunicationTemplates -> "$prefix/communication/templates"

                // Reports
                SchoolDestination.ReportsStudent -> "$prefix/reports/students"
                SchoolDestination.ReportsAcademic -> "$prefix/reports/academics"
                SchoolDestination.ReportsAttendance -> "$prefix/reports/attendance"
                SchoolDestination.ReportsStaff -> "$prefix/reports/staff"
                SchoolDestination.ReportsFinance -> "$prefix/reports/finance"
                SchoolDestination.ReportsAdmission -> "$prefix/reports/admissions"
                SchoolDestination.ReportsLibrary -> "$prefix/reports/library"
                SchoolDestination.ReportsTransport -> "$prefix/reports/transport"
                SchoolDestination.ReportsInventory -> "$prefix/reports/inventory"
                SchoolDestination.ReportsCustom -> "$prefix/reports/custom"

                // Settings - Consolidated
                SchoolDestination.SettingsSchool -> "$prefix/settings/school"
                SchoolDestination.SettingsAcademic -> "$prefix/settings/academic"
                SchoolDestination.SettingsUserManagement -> "$prefix/settings/users"
                SchoolDestination.SettingsRolesPermissions -> "$prefix/settings/roles"
                SchoolDestination.SettingsNotifications -> "$prefix/settings/notifications"
                SchoolDestination.SettingsIntegrations -> "$prefix/settings/integrations"
                SchoolDestination.SettingsBackupRestore -> "$prefix/settings/backup-restore"
                SchoolDestination.SettingsAuditLogs -> "$prefix/settings/audit-logs"

                // Specific sub-settings (kept for back-compatibility/deep-links if needed)
                SchoolDestination.SettingsSchoolProfile -> "$prefix/settings/school/profile"
                SchoolDestination.SettingsSchoolBranches -> "$prefix/settings/school/branches"
                SchoolDestination.SettingsSchoolLogo -> "$prefix/settings/school/logo"
                SchoolDestination.SettingsSchoolContact -> "$prefix/settings/school/contact"
                SchoolDestination.SettingsSchoolWorkingHours -> "$prefix/settings/school/hours"
                SchoolDestination.SettingsAcademicYear -> "$prefix/settings/academic/year"
                SchoolDestination.SettingsAcademicGrading -> "$prefix/settings/academic/grading"
                SchoolDestination.SettingsAcademicAttendance -> "$prefix/settings/academic/attendance"
                SchoolDestination.SettingsAcademicHolidays -> "$prefix/settings/academic/holidays"
                SchoolDestination.SettingsAcademicPromotion -> "$prefix/settings/academic/promotion"
                SchoolDestination.SettingsUserList -> "$prefix/settings/users/list"
                SchoolDestination.SettingsUserInvite -> "$prefix/settings/users/invite"
                SchoolDestination.SettingsUserStatus -> "$prefix/settings/users/status"
                SchoolDestination.SettingsUserPasswordReset -> "$prefix/settings/users/reset"
                SchoolDestination.SettingsRolesList -> "$prefix/settings/roles/list"
                SchoolDestination.SettingsRolesModuleAccess -> "$prefix/settings/roles/access"
                SchoolDestination.SettingsRolesWorkflows -> "$prefix/settings/roles/workflows"
                
                null -> prefix
            }
        }
        null -> "/splash"
    }
    return path
}

data class NavigationState(
    val main: MainDestination,
    val auth: AuthDestination? = null,
    val superAdmin: SuperAdminDestination? = null,
    val school: SchoolDestination? = null
)

fun getDestinationsForPath(path: String): NavigationState {
    val cleanPath = path.removePrefix("#").trim('/').split('?').first().trim('/')
    return when {
        cleanPath == "login" -> NavigationState(MainDestination.Auth, AuthDestination.Login)
        cleanPath.startsWith("superadmin") -> {
            val subPath = cleanPath.removePrefix("superadmin").trim('/')
            val superDest = when (subPath) {
                "dashboard" -> SuperAdminDestination.Dashboard
                "schools" -> SuperAdminDestination.Schools
                else -> SuperAdminDestination.Dashboard
            }
            NavigationState(MainDestination.SuperAdmin, superAdmin = superDest)
        }
        cleanPath.startsWith("school") -> {
            val parts = cleanPath.split('/')
            val schoolId = parts.getOrNull(1) ?: ""
            val subPath = parts.drop(2).joinToString("/")
            val schoolDest = when (subPath) {
                // Dashboard
                "dashboard/overview" -> SchoolDestination.DashboardOverview
                "dashboard/analytics" -> SchoolDestination.DashboardAnalytics
                "dashboard/notifications" -> SchoolDestination.DashboardNotifications
                "dashboard/calendar" -> SchoolDestination.DashboardCalendar
                "dashboard/quick-actions" -> SchoolDestination.DashboardQuickActions
                "dashboard/activities" -> SchoolDestination.DashboardRecentActivities

                // Admissions
                "admissions/enquiries" -> SchoolDestination.AdmissionsEnquiries
                "admissions/applications" -> SchoolDestination.AdmissionsApplications
                "admissions/verification" -> SchoolDestination.AdmissionsDocumentVerification
                "admissions/approval" -> SchoolDestination.AdmissionsApproval
                "admissions/allocation" -> SchoolDestination.AdmissionsAllocation
                "admissions/waiting-list" -> SchoolDestination.AdmissionsWaitingList
                "admissions/reports" -> SchoolDestination.AdmissionsReports

                // Students
                "students/list" -> SchoolDestination.StudentsList
                "students/add" -> SchoolDestination.StudentsAdd
                "students/profile" -> SchoolDestination.StudentsProfile
                "students/documents" -> SchoolDestination.StudentsDocuments
                "students/promotion" -> SchoolDestination.StudentsPromotion
                "students/transfer" -> SchoolDestination.StudentsTransfer
                "students/alumni" -> SchoolDestination.StudentsAlumni
                "students/reports" -> SchoolDestination.StudentsReports

                // Parents
                "parents/list" -> SchoolDestination.ParentsList
                "parents/guardians" -> SchoolDestination.ParentsGuardians
                "parents/portal" -> SchoolDestination.ParentsPortalAccess
                "parents/communication" -> SchoolDestination.ParentsCommunication
                "parents/reports" -> SchoolDestination.ParentsReports

                // Staff
                "staff/employees" -> SchoolDestination.StaffEmployees
                "staff/teachers" -> SchoolDestination.StaffTeachers
                "staff/departments" -> SchoolDestination.StaffDepartments
                "staff/designations" -> SchoolDestination.StaffDesignations
                "staff/leave" -> SchoolDestination.StaffLeave
                "staff/attendance" -> SchoolDestination.StaffAttendance
                "staff/payroll" -> SchoolDestination.StaffPayroll
                "staff/performance" -> SchoolDestination.StaffPerformance
                "staff/recruitment" -> SchoolDestination.StaffRecruitment
                "staff/documents" -> SchoolDestination.StaffDocuments
                "staff/reports" -> SchoolDestination.StaffReports

                // Academics
                "academics/sessions" -> SchoolDestination.AcademicsSessions
                "academics/terms" -> SchoolDestination.AcademicsTerms
                "academics/classes" -> SchoolDestination.AcademicsClasses
                "academics/sections" -> SchoolDestination.AcademicsSections
                "academics/subjects" -> SchoolDestination.AcademicsSubjects
                "academics/subject-groups" -> SchoolDestination.AcademicsSubjectGroups
                "academics/allocation-class" -> SchoolDestination.AcademicsClassTeacherAllocation
                "academics/allocation-subject" -> SchoolDestination.AcademicsSubjectTeacherAllocation
                "academics/curriculum" -> SchoolDestination.AcademicsCurriculum
                "academics/calendar" -> SchoolDestination.AcademicsCalendar

                // Attendance
                "attendance/student" -> SchoolDestination.AttendanceStudent
                "attendance/staff" -> SchoolDestination.AttendanceStaff
                "attendance/biometric" -> SchoolDestination.AttendanceBiometric
                "attendance/reports" -> SchoolDestination.AttendanceReports
                "attendance/settings" -> SchoolDestination.AttendanceSettings

                // Timetable
                "timetable/class" -> SchoolDestination.TimetableClass
                "timetable/teacher" -> SchoolDestination.TimetableTeacher
                "timetable/rooms" -> SchoolDestination.TimetableRooms
                "timetable/periods" -> SchoolDestination.TimetablePeriods
                "timetable/settings" -> SchoolDestination.TimetableSettings

                // Examinations
                "exams/types" -> SchoolDestination.ExamTypes
                "exams/schedule" -> SchoolDestination.ExamSchedule
                "exams/marks" -> SchoolDestination.ExamMarksEntry
                "exams/grading" -> SchoolDestination.ExamGradeSystem
                "exams/results" -> SchoolDestination.ExamResultProcessing
                "exams/report-cards" -> SchoolDestination.ExamReportCards
                "exams/promotion" -> SchoolDestination.ExamPromotion
                "exams/reports" -> SchoolDestination.ExamReports

                // Finance - Fees
                "finance/fees/structures" -> SchoolDestination.FinanceFeesStructures
                "finance/fees/categories" -> SchoolDestination.FinanceFeesCategories
                "finance/fees/discounts" -> SchoolDestination.FinanceFeesDiscounts
                "finance/fees/scholarships" -> SchoolDestination.FinanceFeesScholarships
                "finance/fees/collection" -> SchoolDestination.FinanceFeesCollection
                "finance/fees/due" -> SchoolDestination.FinanceFeesDuePayments
                "finance/fees/receipts" -> SchoolDestination.FinanceFeesReceipts
                "finance/fees/reports" -> SchoolDestination.FinanceFeesReports

                // Finance - Payroll
                "finance/payroll/structures" -> SchoolDestination.FinancePayrollSalaryStructure
                "finance/payroll/processing" -> SchoolDestination.FinancePayrollSalaryProcessing
                "finance/payroll/payslips" -> SchoolDestination.FinancePayrollPayslips
                "finance/payroll/reports" -> SchoolDestination.FinancePayrollReports

                // Finance - Expenses
                "finance/expenses/categories" -> SchoolDestination.FinanceExpensesCategories
                "finance/expenses/list" -> SchoolDestination.FinanceExpensesList
                "finance/expenses/vendors" -> SchoolDestination.FinanceExpensesVendors
                "finance/expenses/reports" -> SchoolDestination.FinanceExpensesReports

                "finance/reports" -> SchoolDestination.FinanceFinancialReports

                // Library
                "library/books" -> SchoolDestination.LibraryBooks
                "library/categories" -> SchoolDestination.LibraryCategories
                "library/authors" -> SchoolDestination.LibraryAuthors
                "library/issue" -> SchoolDestination.LibraryBookIssue
                "library/return" -> SchoolDestination.LibraryBookReturn
                "library/fines" -> SchoolDestination.LibraryFineManagement
                "library/reports" -> SchoolDestination.LibraryReports

                // Transport
                "transport/vehicles" -> SchoolDestination.TransportVehicles
                "transport/drivers" -> SchoolDestination.TransportDrivers
                "transport/routes" -> SchoolDestination.TransportRoutes
                "transport/stops" -> SchoolDestination.TransportStops
                "transport/allocation" -> SchoolDestination.TransportStudentAllocation
                "transport/tracking" -> SchoolDestination.TransportTracking
                "transport/reports" -> SchoolDestination.TransportReports

                // Inventory
                "inventory/assets" -> SchoolDestination.InventoryAssets
                "inventory/categories" -> SchoolDestination.InventoryCategories
                "inventory/stock" -> SchoolDestination.InventoryStock
                "inventory/purchase-orders" -> SchoolDestination.InventoryPurchaseOrders
                "inventory/vendors" -> SchoolDestination.InventoryVendors
                "inventory/assignment" -> SchoolDestination.InventoryAssetAssignment
                "inventory/reports" -> SchoolDestination.InventoryReports

                // Communication
                "communication/announcements" -> SchoolDestination.CommunicationAnnouncements
                "communication/sms" -> SchoolDestination.CommunicationSMS
                "communication/email" -> SchoolDestination.CommunicationEmail
                "communication/push" -> SchoolDestination.CommunicationPushNotifications
                "communication/circulars" -> SchoolDestination.CommunicationCirculars
                "communication/events" -> SchoolDestination.CommunicationEvents
                "communication/notice-board" -> SchoolDestination.CommunicationNoticeBoard
                "communication/templates" -> SchoolDestination.CommunicationTemplates

                // Reports
                "reports/students" -> SchoolDestination.ReportsStudent
                "reports/academics" -> SchoolDestination.ReportsAcademic
                "reports/attendance" -> SchoolDestination.ReportsAttendance
                "reports/staff" -> SchoolDestination.ReportsStaff
                "reports/finance" -> SchoolDestination.ReportsFinance
                "reports/admissions" -> SchoolDestination.ReportsAdmission
                "reports/library" -> SchoolDestination.ReportsLibrary
                "reports/transport" -> SchoolDestination.ReportsTransport
                "reports/inventory" -> SchoolDestination.ReportsInventory
                "reports/custom" -> SchoolDestination.ReportsCustom

                // Settings - Consolidated
                "settings/school" -> SchoolDestination.SettingsSchool
                "settings/academic" -> SchoolDestination.SettingsAcademic
                "settings/users" -> SchoolDestination.SettingsUserManagement
                "settings/roles" -> SchoolDestination.SettingsRolesPermissions
                "settings/notifications" -> SchoolDestination.SettingsNotifications
                "settings/integrations" -> SchoolDestination.SettingsIntegrations
                "settings/backup-restore" -> SchoolDestination.SettingsBackupRestore
                "settings/audit-logs" -> SchoolDestination.SettingsAuditLogs

                // Sub-settings
                "settings/school/profile" -> SchoolDestination.SettingsSchoolProfile
                "settings/school/branches" -> SchoolDestination.SettingsSchoolBranches
                "settings/school/logo" -> SchoolDestination.SettingsSchoolLogo
                "settings/school/contact" -> SchoolDestination.SettingsSchoolContact
                "settings/school/hours" -> SchoolDestination.SettingsSchoolWorkingHours
                "settings/academic/year" -> SchoolDestination.SettingsAcademicYear
                "settings/academic/grading" -> SchoolDestination.SettingsAcademicGrading
                "settings/academic/attendance" -> SchoolDestination.SettingsAcademicAttendance
                "settings/academic/holidays" -> SchoolDestination.SettingsAcademicHolidays
                "settings/academic/promotion" -> SchoolDestination.SettingsAcademicPromotion
                "settings/users/list" -> SchoolDestination.SettingsUserList
                "settings/users/invite" -> SchoolDestination.SettingsUserInvite
                "settings/users/status" -> SchoolDestination.SettingsUserStatus
                "settings/users/reset" -> SchoolDestination.SettingsUserPasswordReset
                "settings/roles/list" -> SchoolDestination.SettingsRolesList
                "settings/roles/permissions" -> SchoolDestination.SettingsRolesPermissions
                "settings/roles/access" -> SchoolDestination.SettingsRolesModuleAccess
                "settings/roles/workflows" -> SchoolDestination.SettingsRolesWorkflows

                else -> SchoolDestination.DashboardOverview
            }
            NavigationState(MainDestination.School(schoolId), school = schoolDest)
        }
        cleanPath == "splash" || cleanPath == "" -> NavigationState(MainDestination.Auth, AuthDestination.Splash)
        else -> NavigationState(MainDestination.Auth, AuthDestination.Splash)
    }
}
