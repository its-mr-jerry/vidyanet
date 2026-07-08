package com.kastack.vidyanet.utils

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePicker(
    onImageSelected: (String) -> Unit,
    onSizeError: ((String) -> Unit)? = null,
    maxSize: Long? = null
): () -> Unit
