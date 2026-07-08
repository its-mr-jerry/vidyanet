package com.kastack.vidyanet.utils

import kotlinx.browser.window

actual class PlatformUtils actual constructor() {
    actual fun readFileData(path: String): FileData? {
        return try {
            if (path.startsWith("data:")) {
                val base64Data = path.substringAfter(",")
                val binaryString = window.atob(base64Data)
                val bytes = ByteArray(binaryString.length) { i ->
                    binaryString[i].code.toByte()
                }
                
                val mimeType = path.substringBefore(";").substringAfter(":")
                val extension = mimeType.substringAfter("/")

                SimpleFileData(bytes, "upload.$extension", mimeType)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
