package com.kastack.vidyanet.navigations

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack

import com.kastack.vidyanet.superAdmin.SuperAdminDestination

@Composable
expect fun BrowserHistorySync(
    mainBackStack: NavBackStack<MainDestination>,
    authBackStack: NavBackStack<AuthDestination>,
    superAdminBackStack: NavBackStack<SuperAdminDestination>
)

fun getPathForDestinations(
    main: MainDestination?,
    auth: AuthDestination?,
    superAdmin: SuperAdminDestination?
): String {
    val path = when (main) {
        MainDestination.Auth -> when (auth) {
            AuthDestination.Splash -> "/splash"
            AuthDestination.Login -> "/login"
            null -> "/splash"
        }
        MainDestination.SuperAdmin -> when (superAdmin) {
            SuperAdminDestination.Dashboard -> "/superadmin/dashboard"
            null -> "/superadmin"
        }
        null -> "/splash"
    }
    return "#$path"
}

data class NavigationState(
    val main: MainDestination,
    val auth: AuthDestination? = null,
    val superAdmin: SuperAdminDestination? = null
)

fun getDestinationsForPath(path: String): NavigationState {
    val cleanPath = path.removePrefix("#").trim('/').split('?').first().trim('/')
    return when {
        cleanPath == "login" -> NavigationState(MainDestination.Auth, AuthDestination.Login)
        cleanPath.startsWith("superadmin") -> {
            val subPath = cleanPath.removePrefix("superadmin").trim('/')
            val superDest = when (subPath) {
                "dashboard" -> SuperAdminDestination.Dashboard
                else -> SuperAdminDestination.Dashboard
            }
            NavigationState(MainDestination.SuperAdmin, superAdmin = superDest)
        }
        cleanPath == "splash" || cleanPath == "" -> NavigationState(MainDestination.Auth, AuthDestination.Splash)
        else -> NavigationState(MainDestination.Auth, AuthDestination.Splash)
    }
}
