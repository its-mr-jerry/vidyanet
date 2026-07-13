package com.kastack.vidyanet.school

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.kastack.vidyanet.school.screens.AcademicSettings
import com.kastack.vidyanet.school.screens.AuditLogs
import com.kastack.vidyanet.school.screens.BackupRestore
import com.kastack.vidyanet.school.screens.Integrations
import com.kastack.vidyanet.school.screens.NotificationSettings
import com.kastack.vidyanet.school.screens.RolesPermissions
import com.kastack.vidyanet.school.screens.SchoolDashboard
import com.kastack.vidyanet.school.screens.SchoolSettings
import com.kastack.vidyanet.school.screens.UserManagement
import com.kastack.vidyanet.commonUi.components.AppText
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment

@Composable
fun SchoolNavHost(
    schoolId: String,
    backStack: NavBackStack<SchoolDestination>,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    SchoolApp(
        schoolId = schoolId,
        currentDestination = backStack.last(),
        onNavigate = { dest -> 
            if (backStack.last() != dest) {
                backStack.add(dest) 
            }
        },
        onLogout = onLogout
    ) { padding ->
        NavDisplay(
            backStack = backStack,
            modifier = modifier.padding(padding)
        ) { destination ->
            NavEntry(destination) {
                when (destination) {
                    SchoolDestination.DashboardOverview -> SchoolDashboard(schoolId = schoolId)
                    SchoolDestination.SettingsSchool -> SchoolSettings(schoolId = schoolId)
                    SchoolDestination.SettingsAcademic -> AcademicSettings(schoolId = schoolId)
                    SchoolDestination.SettingsUserManagement -> UserManagement(schoolId = schoolId)
                    SchoolDestination.SettingsRolesPermissions -> RolesPermissions()
                    SchoolDestination.SettingsNotifications -> NotificationSettings(schoolId = schoolId)
                    SchoolDestination.SettingsIntegrations -> Integrations()
                    SchoolDestination.SettingsBackupRestore -> BackupRestore()
                    SchoolDestination.SettingsAuditLogs -> AuditLogs(schoolId = schoolId)
                    else -> PlaceholderScreen(destination.toString(), schoolId)
                }
            }
        }
    }
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
