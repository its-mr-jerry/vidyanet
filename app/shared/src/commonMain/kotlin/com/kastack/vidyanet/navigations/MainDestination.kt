package com.kastack.vidyanet.navigations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainDestination : NavKey {
    @Serializable
    data object Auth : MainDestination
    @Serializable
    data object SuperAdmin : MainDestination
}
