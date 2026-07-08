package com.kastack.vidyanet

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kastack.vidyanet.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "VidyaNet",
        ) {
            App()
        }
    }
}