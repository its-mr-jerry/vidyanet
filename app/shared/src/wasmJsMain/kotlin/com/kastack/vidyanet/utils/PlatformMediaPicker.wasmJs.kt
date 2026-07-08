package com.kastack.vidyanet.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
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
                    println("Selected file size: ${file.size}, maxSize: $maxSize")
                    if (maxSize != null && file.size.toDouble() > maxSize.toDouble()) {
                        val sizeInKb = maxSize / 1024
                        onSizeError?.invoke("Image size exceeds the limit of $sizeInKb KB")
                    } else {
                        val reader = FileReader()
                        reader.onload = {
                            val result = reader.result
                            if (result != null) {
                                // Safe way to convert JS string to Kotlin string in Wasm
                                val resultStr = convertJsAnyToString(result)
                                onImageSelected(resultStr)
                            }
                        }
                        reader.readAsDataURL(file)
                    }
                }
            }
            input.click()
        }
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun convertJsAnyToString(jsAny: JsAny): String = 
    js("String(jsAny)")
