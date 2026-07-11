package com.kastack.vidyanet.school

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface SchoolDestination : NavKey {
    // Dashboard
    @Serializable data object DashboardOverview : SchoolDestination
    @Serializable data object DashboardAnalytics : SchoolDestination
    @Serializable data object DashboardNotifications : SchoolDestination
    @Serializable data object DashboardCalendar : SchoolDestination
    @Serializable data object DashboardQuickActions : SchoolDestination
    @Serializable data object DashboardRecentActivities : SchoolDestination

    // Admissions
    @Serializable data object AdmissionsEnquiries : SchoolDestination
    @Serializable data object AdmissionsApplications : SchoolDestination
    @Serializable data object AdmissionsDocumentVerification : SchoolDestination
    @Serializable data object AdmissionsApproval : SchoolDestination
    @Serializable data object AdmissionsAllocation : SchoolDestination
    @Serializable data object AdmissionsWaitingList : SchoolDestination
    @Serializable data object AdmissionsReports : SchoolDestination

    // Students
    @Serializable data object StudentsList : SchoolDestination
    @Serializable data object StudentsAdd : SchoolDestination
    @Serializable data object StudentsProfile : SchoolDestination
    @Serializable data object StudentsDocuments : SchoolDestination
    @Serializable data object StudentsPromotion : SchoolDestination
    @Serializable data object StudentsTransfer : SchoolDestination
    @Serializable data object StudentsAlumni : SchoolDestination
    @Serializable data object StudentsReports : SchoolDestination

    // Parents
    @Serializable data object ParentsList : SchoolDestination
    @Serializable data object ParentsGuardians : SchoolDestination
    @Serializable data object ParentsPortalAccess : SchoolDestination
    @Serializable data object ParentsCommunication : SchoolDestination
    @Serializable data object ParentsReports : SchoolDestination

    // Staff
    @Serializable data object StaffEmployees : SchoolDestination
    @Serializable data object StaffTeachers : SchoolDestination
    @Serializable data object StaffDepartments : SchoolDestination
    @Serializable data object StaffDesignations : SchoolDestination
    @Serializable data object StaffLeave : SchoolDestination
    @Serializable data object StaffAttendance : SchoolDestination
    @Serializable data object StaffPayroll : SchoolDestination
    @Serializable data object StaffPerformance : SchoolDestination
    @Serializable data object StaffRecruitment : SchoolDestination
    @Serializable data object StaffDocuments : SchoolDestination
    @Serializable data object StaffReports : SchoolDestination

    // Academics
    @Serializable data object AcademicsSessions : SchoolDestination
    @Serializable data object AcademicsTerms : SchoolDestination
    @Serializable data object AcademicsClasses : SchoolDestination
    @Serializable data object AcademicsSections : SchoolDestination
    @Serializable data object AcademicsSubjects : SchoolDestination
    @Serializable data object AcademicsSubjectGroups : SchoolDestination
    @Serializable data object AcademicsClassTeacherAllocation : SchoolDestination
    @Serializable data object AcademicsSubjectTeacherAllocation : SchoolDestination
    @Serializable data object AcademicsCurriculum : SchoolDestination
    @Serializable data object AcademicsCalendar : SchoolDestination

    // Attendance
    @Serializable data object AttendanceStudent : SchoolDestination
    @Serializable data object AttendanceStaff : SchoolDestination
    @Serializable data object AttendanceBiometric : SchoolDestination
    @Serializable data object AttendanceReports : SchoolDestination
    @Serializable data object AttendanceSettings : SchoolDestination

    // Timetable
    @Serializable data object TimetableClass : SchoolDestination
    @Serializable data object TimetableTeacher : SchoolDestination
    @Serializable data object TimetableRooms : SchoolDestination
    @Serializable data object TimetablePeriods : SchoolDestination
    @Serializable data object TimetableSettings : SchoolDestination

    // Examinations
    @Serializable data object ExamTypes : SchoolDestination
    @Serializable data object ExamSchedule : SchoolDestination
    @Serializable data object ExamMarksEntry : SchoolDestination
    @Serializable data object ExamGradeSystem : SchoolDestination
    @Serializable data object ExamResultProcessing : SchoolDestination
    @Serializable data object ExamReportCards : SchoolDestination
    @Serializable data object ExamPromotion : SchoolDestination
    @Serializable data object ExamReports : SchoolDestination

    // Finance - Fees
    @Serializable data object FinanceFeesStructures : SchoolDestination
    @Serializable data object FinanceFeesCategories : SchoolDestination
    @Serializable data object FinanceFeesDiscounts : SchoolDestination
    @Serializable data object FinanceFeesScholarships : SchoolDestination
    @Serializable data object FinanceFeesCollection : SchoolDestination
    @Serializable data object FinanceFeesDuePayments : SchoolDestination
    @Serializable data object FinanceFeesReceipts : SchoolDestination
    @Serializable data object FinanceFeesReports : SchoolDestination

    // Finance - Payroll
    @Serializable data object FinancePayrollSalaryStructure : SchoolDestination
    @Serializable data object FinancePayrollSalaryProcessing : SchoolDestination
    @Serializable data object FinancePayrollPayslips : SchoolDestination
    @Serializable data object FinancePayrollReports : SchoolDestination

    // Finance - Expenses
    @Serializable data object FinanceExpensesCategories : SchoolDestination
    @Serializable data object FinanceExpensesList : SchoolDestination
    @Serializable data object FinanceExpensesVendors : SchoolDestination
    @Serializable data object FinanceExpensesReports : SchoolDestination

    @Serializable data object FinanceFinancialReports : SchoolDestination

    // Library
    @Serializable data object LibraryBooks : SchoolDestination
    @Serializable data object LibraryCategories : SchoolDestination
    @Serializable data object LibraryAuthors : SchoolDestination
    @Serializable data object LibraryBookIssue : SchoolDestination
    @Serializable data object LibraryBookReturn : SchoolDestination
    @Serializable data object LibraryFineManagement : SchoolDestination
    @Serializable data object LibraryReports : SchoolDestination

    // Transport
    @Serializable data object TransportVehicles : SchoolDestination
    @Serializable data object TransportDrivers : SchoolDestination
    @Serializable data object TransportRoutes : SchoolDestination
    @Serializable data object TransportStops : SchoolDestination
    @Serializable data object TransportStudentAllocation : SchoolDestination
    @Serializable data object TransportTracking : SchoolDestination
    @Serializable data object TransportReports : SchoolDestination

    // Inventory
    @Serializable data object InventoryAssets : SchoolDestination
    @Serializable data object InventoryCategories : SchoolDestination
    @Serializable data object InventoryStock : SchoolDestination
    @Serializable data object InventoryPurchaseOrders : SchoolDestination
    @Serializable data object InventoryVendors : SchoolDestination
    @Serializable data object InventoryAssetAssignment : SchoolDestination
    @Serializable data object InventoryReports : SchoolDestination

    // Communication
    @Serializable data object CommunicationAnnouncements : SchoolDestination
    @Serializable data object CommunicationSMS : SchoolDestination
    @Serializable data object CommunicationEmail : SchoolDestination
    @Serializable data object CommunicationPushNotifications : SchoolDestination
    @Serializable data object CommunicationCirculars : SchoolDestination
    @Serializable data object CommunicationEvents : SchoolDestination
    @Serializable data object CommunicationNoticeBoard : SchoolDestination
    @Serializable data object CommunicationTemplates : SchoolDestination

    // Reports
    @Serializable data object ReportsStudent : SchoolDestination
    @Serializable data object ReportsAcademic : SchoolDestination
    @Serializable data object ReportsAttendance : SchoolDestination
    @Serializable data object ReportsStaff : SchoolDestination
    @Serializable data object ReportsFinance : SchoolDestination
    @Serializable data object ReportsAdmission : SchoolDestination
    @Serializable data object ReportsLibrary : SchoolDestination
    @Serializable data object ReportsTransport : SchoolDestination
    @Serializable data object ReportsInventory : SchoolDestination
    @Serializable data object ReportsCustom : SchoolDestination

    // Consolidated Settings Screens
    @Serializable data object SettingsSchool : SchoolDestination
    @Serializable data object SettingsAcademic : SchoolDestination
    @Serializable data object SettingsUserManagement : SchoolDestination
    @Serializable data object SettingsRolesPermissions : SchoolDestination
    @Serializable data object SettingsNotifications : SchoolDestination
    @Serializable data object SettingsIntegrations : SchoolDestination
    @Serializable data object SettingsBackupRestore : SchoolDestination
    @Serializable data object SettingsAuditLogs : SchoolDestination

    // Keep sub-settings for direct URL access if needed, but sidebar will use parent ones.
    @Serializable data object SettingsSchoolProfile : SchoolDestination
    @Serializable data object SettingsSchoolBranches : SchoolDestination
    @Serializable data object SettingsSchoolLogo : SchoolDestination
    @Serializable data object SettingsSchoolContact : SchoolDestination
    @Serializable data object SettingsSchoolWorkingHours : SchoolDestination
    @Serializable data object SettingsAcademicYear : SchoolDestination
    @Serializable data object SettingsAcademicGrading : SchoolDestination
    @Serializable data object SettingsAcademicAttendance : SchoolDestination
    @Serializable data object SettingsAcademicHolidays : SchoolDestination
    @Serializable data object SettingsAcademicPromotion : SchoolDestination
    @Serializable data object SettingsUserList : SchoolDestination
    @Serializable data object SettingsUserInvite : SchoolDestination
    @Serializable data object SettingsUserStatus : SchoolDestination
    @Serializable data object SettingsUserPasswordReset : SchoolDestination
    @Serializable data object SettingsRolesList : SchoolDestination
    @Serializable data object SettingsRolesModuleAccess : SchoolDestination
    @Serializable data object SettingsRolesWorkflows : SchoolDestination
}
