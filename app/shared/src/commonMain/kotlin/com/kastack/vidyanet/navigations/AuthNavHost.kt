package com.kastack.vidyanet.navigations

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.kastack.vidyanet.commonUi.screens.LoginScreen
import com.kastack.vidyanet.commonUi.screens.SplashScreen

@Composable
fun AuthNavHost(
    backStack: NavBackStack<AuthDestination>,
    onAuthenticated: (MainDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier
    ) { destination ->
        NavEntry(destination) {
            when (destination) {
                AuthDestination.Splash -> SplashScreen(
                    onNavigate = onAuthenticated,
                    onLoginRequired = {
                        backStack.clear()
                        backStack.add(AuthDestination.Login)
                    }
                )
                AuthDestination.Login -> LoginScreen(
                    onAuthenticated = onAuthenticated
                )
            }
        }
    }
}
