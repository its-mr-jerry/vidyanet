package com.kastack.vidyanet.navigations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import kotlinx.browser.window
import org.w3c.dom.events.Event

import com.kastack.vidyanet.superAdmin.SuperAdminDestination
import com.kastack.vidyanet.school.SchoolDestination

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
    val hashPath = "#$currentPath"
    
    val previousMain = remember { mutableStateOf<MainDestination?>(null) }

    // Update URL when state changes
    LaunchedEffect(hashPath) {
        if (window.location.hash != hashPath) {
            val isFromAuthToPanel = previousMain.value == MainDestination.Auth && 
                                   (currentMain is MainDestination.School || currentMain == MainDestination.SuperAdmin)
            
            // Use replaceState for transitions that should not be in history
            val isReplacing = isFromAuthToPanel || 
                             currentAuth == AuthDestination.Login || 
                             currentAuth == AuthDestination.Splash ||
                             window.location.hash.isEmpty() ||
                             window.location.hash == "#/"

            if (isReplacing) {
                window.history.replaceState(null, "", hashPath)
            } else {
                window.location.hash = hashPath
            }
        }
        previousMain.value = currentMain
    }

    // Update state when URL changes (back/forward/manual entry)
    DisposableEffect(Unit) {
        val onHashChange = { _: Event ->
            val state = getDestinationsForPath(window.location.hash)
            
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
        window.addEventListener("hashchange", onHashChange)
        
        // Handle initial path
        val initialPath = window.location.hash.ifEmpty { hashPath }
        val state = getDestinationsForPath(initialPath)
        
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
            window.removeEventListener("hashchange", onHashChange)
        }
    }
}
