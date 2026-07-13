package com.kastack.vidyanet

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import org.koin.compose.koinInject

import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import com.kastack.vidyanet.core.GlobalStore
import com.kastack.vidyanet.data.DatabaseManager
import com.kastack.vidyanet.data.repositories.UserRepository
import org.koin.compose.koinInject
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun App(
    initialMain: MainDestination = MainDestination.Auth,
    initialAuth: AuthDestination = AuthDestination.Splash,
    initialSuperAdmin: SuperAdminDestination = SuperAdminDestination.Dashboard,
    initialSchool: SchoolDestination = SchoolDestination.DashboardOverview
) {
    val globalStore: GlobalStore = koinInject()
    val userRepository: UserRepository = koinInject()
    val databaseManager: DatabaseManager = koinInject()
    
    val currentUser by globalStore.currentUser.collectAsState()
    val isInitializing by globalStore.isInitializing.collectAsState()

    val mainBackStack = remember { NavBackStack(initialMain) }
    val authBackStack = remember { NavBackStack(initialAuth) }
    val superAdminBackStack = remember { NavBackStack(initialSuperAdmin) }
    val schoolBackStack = remember { NavBackStack(initialSchool) }

    // Initial Auth Check on Boot/Reload
    LaunchedEffect(Unit) {
        val token = databaseManager.getString("auth_token", "")
        if (token.isNotEmpty()) {
            userRepository.getMe().onSuccess { user ->
                globalStore.updateCurrentUser(user)
            }.onFailure {
                databaseManager.remove("auth_token")
                globalStore.clear()
            }
        }
        // Small delay to ensure everything is ready
        delay(500.milliseconds)
        globalStore.setInitializing(false)
    }

    // Guard: Redirect to Auth if not logged in but trying to access protected route
    LaunchedEffect(currentUser, isInitializing, mainBackStack.last()) {
        if (!isInitializing && currentUser == null && mainBackStack.last() != MainDestination.Auth) {
            mainBackStack.clear()
            mainBackStack.add(MainDestination.Auth)
            authBackStack.clear()
            authBackStack.add(AuthDestination.Login)
        }
    }

    BrowserHistorySync(
        mainBackStack = mainBackStack,
        authBackStack = authBackStack,
        superAdminBackStack = superAdminBackStack,
        schoolBackStack = schoolBackStack,
        isAllowed = { state ->
            if (isInitializing) true // Allow state reconstruction during init
            else if (state.main == MainDestination.Auth) true
            else currentUser != null
        }
    )

    VidyaNetTheme {
        if (isInitializing) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            when (val current = mainBackStack.last()) {
                MainDestination.Auth -> {
                    AuthNavHost(
                        backStack = authBackStack,
                        onAuthenticated = { destination ->
                            mainBackStack.clear()
                            mainBackStack.add(destination)
                            
                            authBackStack.clear()
                            authBackStack.add(AuthDestination.Login)
                        }
                    )
                }
                MainDestination.SuperAdmin -> {
                   SuperAdminNavHost(
                       backStack = superAdminBackStack,
                       onLogout = {
                           mainBackStack.clear()
                           mainBackStack.add(MainDestination.Auth)
                           
                           authBackStack.clear()
                           authBackStack.add(AuthDestination.Login)

                           superAdminBackStack.clear()
                           superAdminBackStack.add(initialSuperAdmin)
                           schoolBackStack.clear()
                           schoolBackStack.add(initialSchool)
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
                            mainBackStack.clear()
                            mainBackStack.add(MainDestination.Auth)
                            
                            authBackStack.clear()
                            authBackStack.add(AuthDestination.Login)

                            schoolBackStack.clear()
                            schoolBackStack.add(initialSchool)
                            superAdminBackStack.clear()
                            superAdminBackStack.add(initialSuperAdmin)
                        }
                    )
                }
            }
        }
    }
}
