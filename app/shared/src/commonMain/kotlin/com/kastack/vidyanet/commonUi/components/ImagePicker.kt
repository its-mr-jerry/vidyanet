package com.kastack.vidyanet.commonUi.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch

@Composable
fun rememberImagePicker(
    maxSizeMB: Int = 1,
    onImageSelected: (ByteArray?) -> Unit,
    onSizeExceeded: (() -> Unit)? = null
): () -> Unit {
    val scope = rememberCoroutineScope()
    val launcher = rememberFilePickerLauncher(
        type = PickerType.Image,
        mode = PickerMode.Single,
        onResult = { file ->
            scope.launch {
                val bytes = file?.readBytes()
                if (bytes != null && bytes.size > maxSizeMB * 1024 * 1024) {
                    onSizeExceeded?.invoke()
                } else {
                    onImageSelected(bytes)
                }
            }
        }
    )
    return { launcher.launch() }
}
