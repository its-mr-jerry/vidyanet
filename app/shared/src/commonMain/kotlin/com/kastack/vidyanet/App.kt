package com.kastack.vidyanet

import androidx.compose.runtime.*
import com.kastack.vidyanet.navigations.AuthDestination
import com.kastack.vidyanet.navigations.AuthNavHost
import com.kastack.vidyanet.navigations.MainDestination
import com.kastack.vidyanet.theme.VidyaNetTheme
import androidx.navigation3.runtime.NavBackStack

import com.kastack.vidyanet.navigations.BrowserHistorySync

import com.kastack.vidyanet.superAdmin.screens.SuperAdminDashboard

import com.kastack.vidyanet.superAdmin.SuperAdminDestination
import com.kastack.vidyanet.superAdmin.SuperAdminNavHost
import com.kastack.vidyanet.school.SchoolDestination
import com.kastack.vidyanet.school.SchoolNavHost

@Composable
fun App(
    initialMain: MainDestination = MainDestination.Auth,
    initialAuth: AuthDestination = AuthDestination.Splash,
    initialSuperAdmin: SuperAdminDestination = SuperAdminDestination.Dashboard,
    initialSchool: SchoolDestination = SchoolDestination.DashboardOverview
) {
    val mainBackStack = remember { NavBackStack(initialMain) }
    val authBackStack = remember { NavBackStack(initialAuth) }
    val superAdminBackStack = remember { NavBackStack(initialSuperAdmin) }
    val schoolBackStack = remember { NavBackStack(initialSchool) }

    BrowserHistorySync(mainBackStack, authBackStack, superAdminBackStack, schoolBackStack)

    VidyaNetTheme {
        when (val current = mainBackStack.last()) {
            MainDestination.Auth -> {
                AuthNavHost(
                    backStack = authBackStack,
                    onAuthenticated = { destination ->
                        mainBackStack.clear()
                        mainBackStack.add(destination)
                    }
                )
            }
            MainDestination.SuperAdmin -> {
               SuperAdminNavHost(
                   backStack = superAdminBackStack,
                   onNavigateToSchool = { schoolId ->
                       mainBackStack.add(MainDestination.School(schoolId))
                   }
               )
            }
            is MainDestination.School -> {
                SchoolNavHost(
                    schoolId = current.schoolId,
                    backStack = schoolBackStack
                )
            }
        }
    }
}
