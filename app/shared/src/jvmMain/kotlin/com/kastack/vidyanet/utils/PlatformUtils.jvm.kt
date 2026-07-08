package com.kastack.vidyanet.utils

import java.io.File
import java.net.URLConnection

actual class PlatformUtils actual constructor() {
    actual fun readFileData(path: String): FileData? {
        return try {
            if (path.startsWith("data:")) {
                val base64Data = path.substringAfter(",")
                val bytes = java.util.Base64.getDecoder().decode(base64Data)
                val mimeType = path.substringBefore(";").substringAfter(":")
                val extension = mimeType.substringAfter("/")
                SimpleFileData(bytes, "upload.$extension", mimeType)
            } else {
                val file = File(path)
                if (!file.exists()) return null
                
                val bytes = file.readBytes()
                val name = file.name
                val contentType = URLConnection.guessContentTypeFromName(name) ?: "application/octet-stream"

                SimpleFileData(bytes, name, contentType)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
