package com.kastack.vidyanet

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.kastack.vidyanet.di.initKoin
import kotlinx.browser.document

import com.kastack.vidyanet.navigations.getDestinationsForPath
import com.kastack.vidyanet.navigations.AuthDestination
import com.kastack.vidyanet.superAdmin.SuperAdminDestination
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()
    val state = getDestinationsForPath(window.location.hash)
    ComposeViewport {
        App(
            initialMain = state.main,
            initialAuth = state.auth ?: AuthDestination.Splash,
            initialSuperAdmin = state.superAdmin ?: SuperAdminDestination.Dashboard
        )
    }
}
