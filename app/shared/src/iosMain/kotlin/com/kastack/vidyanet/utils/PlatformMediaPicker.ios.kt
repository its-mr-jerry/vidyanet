package com.kastack.vidyanet.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberImagePicker(
    onImageSelected: (String) -> Unit,
    onSizeError: ((String) -> Unit)?,
    maxSize: Long?
): () -> Unit {
    return remember {
        {
            // TODO: Implement image picker for iOS
        }
    }
}
