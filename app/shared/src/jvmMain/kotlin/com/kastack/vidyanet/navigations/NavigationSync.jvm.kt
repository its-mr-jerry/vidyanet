package com.kastack.vidyanet.navigations

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack

import com.kastack.vidyanet.superAdmin.SuperAdminDestination

@Composable
actual fun BrowserHistorySync(
    mainBackStack: NavBackStack<MainDestination>,
    authBackStack: NavBackStack<AuthDestination>,
    superAdminBackStack: NavBackStack<SuperAdminDestination>
) {
    // No-op on JVM
}
