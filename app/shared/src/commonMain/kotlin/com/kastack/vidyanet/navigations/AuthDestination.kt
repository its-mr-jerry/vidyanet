package com.kastack.vidyanet.navigations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthDestination : NavKey {
    @Serializable
    data object Splash : AuthDestination
    @Serializable
    data object Login : AuthDestination
}
