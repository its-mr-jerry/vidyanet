package com.kastack.vidyanet.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader

@Composable
actual fun rememberImagePicker(
    onImageSelected: (String) -> Unit,
    onSizeError: ((String) -> Unit)?,
    maxSize: Long?
): () -> Unit {
    return remember(onImageSelected, onSizeError, maxSize) {
        {
            val input = document.createElement("input") as HTMLInputElement
            input.type = "file"
            input.accept = "image/*"
            input.onchange = {
                val file = input.files?.item(0)
                if (file != null) {
                    if (maxSize != null && file.size.toLong() > maxSize) {
                        val sizeInKb = maxSize / 1024
                        onSizeError?.invoke("Image size exceeds the limit of $sizeInKb KB")
                    } else {
                        val reader = FileReader()
                        reader.onload = {
                            val result = reader.result.toString()
                            onImageSelected(result)
                        }
                        reader.readAsDataURL(file)
                    }
                }
            }
            input.click()
        }
    }
}
