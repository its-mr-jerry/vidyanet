package com.kastack.vidyanet.superAdmin

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface SuperAdminDestination : NavKey {
    @Serializable
    data object Dashboard : SuperAdminDestination
    
    // Future destinations
    // @Serializable
    // data object Schools : SuperAdminDestination
    // @Serializable
    // data object Admins : SuperAdminDestination
}
