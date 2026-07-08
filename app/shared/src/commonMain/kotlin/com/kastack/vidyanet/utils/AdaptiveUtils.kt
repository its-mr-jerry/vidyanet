package com.kastack.vidyanet.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.BoxWithConstraints

enum class WindowSizeClass {
    COMPACT, MEDIUM, EXPANDED
}

@Composable
fun rememberWindowSizeClass(width: Dp): WindowSizeClass {
    return when {
        width < 600.dp -> WindowSizeClass.COMPACT
        width < 840.dp -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }
}

@Composable
fun AdaptiveLayout(
    content: @Composable (WindowSizeClass) -> Unit
) {
    BoxWithConstraints {
        val windowSizeClass = rememberWindowSizeClass(maxWidth)
        content(windowSizeClass)
    }
}
