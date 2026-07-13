package com.kastack.vidyanet.navigations

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack

import com.kastack.vidyanet.superAdmin.SuperAdminDestination
import com.kastack.vidyanet.school.SchoolDestination

@Composable
actual fun BrowserHistorySync(
    mainBackStack: NavBackStack<MainDestination>,
    authBackStack: NavBackStack<AuthDestination>,
    superAdminBackStack: NavBackStack<SuperAdminDestination>,
    schoolBackStack: NavBackStack<SchoolDestination>,
    isAllowed: (NavigationState) -> Boolean
) {
    // No-op on iOS
}
