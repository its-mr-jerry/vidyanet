package com.kastack.vidyanet.di

import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun KoinPreviewContainer(content: @Composable () -> Unit) {
    KoinApplication(
        configuration = koinConfiguration(declaration = { modules(appModules) }),
        content = content
    )
}
