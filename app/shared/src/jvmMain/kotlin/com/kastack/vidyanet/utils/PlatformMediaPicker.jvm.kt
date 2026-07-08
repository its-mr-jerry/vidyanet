package com.kastack.vidyanet.utils

import androidx.compose.runtime.Composable
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.nio.file.Files
import java.util.Base64

@Composable
actual fun rememberImagePicker(
    onImageSelected: (String) -> Unit,
    onSizeError: ((String) -> Unit)?,
    maxSize: Long?
): () -> Unit {
    return {
        val dialog = FileDialog(null as Frame?, "Select Image", FileDialog.LOAD)
        dialog.setFilenameFilter { _, name ->
            val lower = name.lowercase()
            lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")
        }
        dialog.isVisible = true
        if (dialog.file != null) {
            val file = File(dialog.directory + dialog.file)
            if (maxSize != null && file.length() > maxSize) {
                val sizeInKb = maxSize / 1024
                onSizeError?.invoke("Image size exceeds the limit of $sizeInKb KB")
            } else {
                val bytes = Files.readAllBytes(file.toPath())
                val base64 = Base64.getEncoder().encodeToString(bytes)
                val mimeType = when {
                    file.name.lowercase().endsWith(".png") -> "image/png"
                    else -> "image/jpeg"
                }
                onImageSelected("data:$mimeType;base64,$base64")
            }
        }
    }
}
