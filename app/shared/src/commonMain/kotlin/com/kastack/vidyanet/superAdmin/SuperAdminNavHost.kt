package com.kastack.vidyanet.superAdmin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.kastack.vidyanet.superAdmin.screens.SuperAdminDashboard

@Composable
fun SuperAdminNavHost(
    backStack: NavBackStack<SuperAdminDestination>,
    modifier: Modifier = Modifier
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier
    ) { destination ->
        NavEntry(destination) {
            when (destination) {
                SuperAdminDestination.Dashboard -> SuperAdminDashboard()
            }
        }
    }
}
