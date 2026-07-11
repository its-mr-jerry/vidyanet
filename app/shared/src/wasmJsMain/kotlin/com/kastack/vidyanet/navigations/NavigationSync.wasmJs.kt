package com.kastack.vidyanet.navigations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation3.runtime.NavBackStack
import kotlinx.browser.window
import org.w3c.dom.events.Event

import com.kastack.vidyanet.superAdmin.SuperAdminDestination
import com.kastack.vidyanet.school.SchoolDestination

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@Composable
actual fun BrowserHistorySync(
    mainBackStack: NavBackStack<MainDestination>,
    authBackStack: NavBackStack<AuthDestination>,
    superAdminBackStack: NavBackStack<SuperAdminDestination>,
    schoolBackStack: NavBackStack<SchoolDestination>
) {
    val currentMain = try { mainBackStack.last() } catch (e: Exception) { null }
    val currentAuth = if (currentMain == MainDestination.Auth) {
        try { authBackStack.last() } catch (e: Exception) { null }
    } else null
    val currentSuperAdmin = if (currentMain == MainDestination.SuperAdmin) {
        try { superAdminBackStack.last() } catch (e: Exception) { null }
    } else null
    val currentSchool = if (currentMain is MainDestination.School) {
        try { schoolBackStack.last() } catch (e: Exception) { null }
    } else null

    val currentPath = getPathForDestinations(currentMain, currentAuth, currentSuperAdmin, currentSchool)

    // Update URL when state changes
    LaunchedEffect(currentPath) {
        if (window.location.pathname != currentPath) {
            window.history.pushState(null, "", currentPath)
        }
    }

    // Update state when URL changes (back/forward)
    DisposableEffect(Unit) {
        val onPopState = { _: Event ->
            val state = getDestinationsForPath(window.location.pathname)
            
            if (mainBackStack.last() != state.main) {
                mainBackStack.clear()
                mainBackStack.add(state.main)
            }
            if (state.auth != null && authBackStack.last() != state.auth) {
                authBackStack.clear()
                authBackStack.add(state.auth)
            }
            if (state.superAdmin != null && superAdminBackStack.last() != state.superAdmin) {
                superAdminBackStack.clear()
                superAdminBackStack.add(state.superAdmin)
            }
            if (state.school != null && schoolBackStack.last() != state.school) {
                schoolBackStack.clear()
                schoolBackStack.add(state.school)
            }
        }
        window.addEventListener("popstate", onPopState)
        
        // Handle initial path
        val state = getDestinationsForPath(window.location.pathname)
        if (mainBackStack.last() != state.main) {
            mainBackStack.clear()
            mainBackStack.add(state.main)
        }
        if (state.auth != null && authBackStack.last() != state.auth) {
            authBackStack.clear()
            authBackStack.add(state.auth)
        }
        if (state.superAdmin != null && superAdminBackStack.last() != state.superAdmin) {
            superAdminBackStack.clear()
            superAdminBackStack.add(state.superAdmin)
        }
        if (state.school != null && schoolBackStack.last() != state.school) {
            schoolBackStack.clear()
            schoolBackStack.add(state.school)
        }

        onDispose {
            window.removeEventListener("popstate", onPopState)
        }
    }
}
