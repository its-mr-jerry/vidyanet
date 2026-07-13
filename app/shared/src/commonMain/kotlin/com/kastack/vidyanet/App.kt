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
import com.kastack.vidyanet.core.GlobalStore
import org.koin.compose.koinInject

@Composable
fun App(
    initialMain: MainDestination = MainDestination.Auth,
    initialAuth: AuthDestination = AuthDestination.Splash,
    initialSuperAdmin: SuperAdminDestination = SuperAdminDestination.Dashboard,
    initialSchool: SchoolDestination = SchoolDestination.DashboardOverview
) {
    val globalStore: GlobalStore = koinInject()
    val currentUser by globalStore.currentUser.collectAsState()

    val mainBackStack = remember { NavBackStack(initialMain) }
    val authBackStack = remember { NavBackStack(initialAuth) }
    val superAdminBackStack = remember { NavBackStack(initialSuperAdmin) }
    val schoolBackStack = remember { NavBackStack(initialSchool) }

    // Guard: Redirect to Auth if not logged in but trying to access protected route
    LaunchedEffect(currentUser, mainBackStack.last()) {
        if (currentUser == null && mainBackStack.last() != MainDestination.Auth) {
            mainBackStack.clear()
            mainBackStack.add(MainDestination.Auth)
            authBackStack.clear()
            authBackStack.add(AuthDestination.Login)
        }
    }

    BrowserHistorySync(mainBackStack, authBackStack, superAdminBackStack, schoolBackStack)

    VidyaNetTheme {
        when (val current = mainBackStack.last()) {
            MainDestination.Auth -> {
                AuthNavHost(
                    backStack = authBackStack,
                    onAuthenticated = { destination ->
                        // Clear Auth sub-stack when moving to a panel
                        authBackStack.clear()
                        authBackStack.add(initialAuth) // Reset to Splash for next time
                        
                        mainBackStack.clear()
                        mainBackStack.add(destination)
                    }
                )
            }
            MainDestination.SuperAdmin -> {
               SuperAdminNavHost(
                   backStack = superAdminBackStack,
                   onLogout = {
                       // Reset all sub-stacks
                       superAdminBackStack.clear()
                       superAdminBackStack.add(initialSuperAdmin)
                       schoolBackStack.clear()
                       schoolBackStack.add(initialSchool)
                       
                       authBackStack.clear()
                       authBackStack.add(AuthDestination.Login)
                       
                       mainBackStack.clear()
                       mainBackStack.add(MainDestination.Auth)
                   },
                   onNavigateToSchool = { schoolId ->
                       mainBackStack.add(MainDestination.School(schoolId))
                   }
               )
            }
            is MainDestination.School -> {
                SchoolNavHost(
                    schoolId = current.schoolId,
                    backStack = schoolBackStack,
                    onLogout = {
                        // Reset all sub-stacks
                        schoolBackStack.clear()
                        schoolBackStack.add(initialSchool)
                        superAdminBackStack.clear()
                        superAdminBackStack.add(initialSuperAdmin)
                        
                        authBackStack.clear()
                        authBackStack.add(AuthDestination.Login)
                        
                        mainBackStack.clear()
                        mainBackStack.add(MainDestination.Auth)
                    }
                )
            }
        }
    }
}
