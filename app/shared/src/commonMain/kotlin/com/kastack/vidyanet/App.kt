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

@Composable
fun App(
    initialMain: MainDestination = MainDestination.Auth,
    initialAuth: AuthDestination = AuthDestination.Splash,
    initialSuperAdmin: SuperAdminDestination = SuperAdminDestination.Dashboard
) {
    val mainBackStack = remember { NavBackStack<MainDestination>(initialMain) }
    val authBackStack = remember { NavBackStack<AuthDestination>(initialAuth) }
    val superAdminBackStack = remember { NavBackStack<SuperAdminDestination>(initialSuperAdmin) }

    BrowserHistorySync(mainBackStack, authBackStack, superAdminBackStack)

    VidyaNetTheme {
        when (mainBackStack.last()) {
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
               SuperAdminNavHost(backStack = superAdminBackStack)
            }
        }
    }
}
