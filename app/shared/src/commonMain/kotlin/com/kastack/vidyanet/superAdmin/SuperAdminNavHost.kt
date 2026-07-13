package com.kastack.vidyanet.superAdmin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.kastack.vidyanet.superAdmin.screens.SchoolsScreen
import com.kastack.vidyanet.superAdmin.screens.SuperAdminDashboard

@Composable
fun SuperAdminNavHost(
    backStack: NavBackStack<SuperAdminDestination>,
    onLogout: () -> Unit,
    onNavigateToSchool: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier
    ) { destination ->
        NavEntry(destination) {
            when (destination) {
                SuperAdminDestination.Dashboard -> SuperAdminDashboard(
                    onNavigate = { dest ->
                        if (dest == "Schools") {
                            backStack.add(SuperAdminDestination.Schools)
                        }
                    },
                    onLogout = onLogout
                )
                SuperAdminDestination.Schools -> SchoolsScreen(
                    onNavigate = { dest ->
                        if (dest == "Dashboard") {
                            backStack.clear()
                            backStack.add(SuperAdminDestination.Dashboard)
                        }
                    },
                    onLogout = onLogout,
                    onSchoolClick = onNavigateToSchool
                )
            }
        }
    }
}
