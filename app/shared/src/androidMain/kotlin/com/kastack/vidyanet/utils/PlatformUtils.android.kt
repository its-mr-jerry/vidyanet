package com.kastack.vidyanet.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class PlatformUtils actual constructor() : KoinComponent {
    private val context: Context by inject()

    actual fun readFileData(path: String): FileData? {
        return try {
            if (path.startsWith("data:")) {
                val base64Data = path.substringAfter(",")
                val bytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                val mimeType = path.substringBefore(";").substringAfter(":")
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"
                SimpleFileData(bytes, "upload.$extension", mimeType)
            } else {
                val uri = Uri.parse(path)
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: return null
                inputStream.close()
                
                val name = path.substringAfterLast("/")
                val extension = MimeTypeMap.getFileExtensionFromUrl(path)
                val contentType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "application/octet-stream"

                SimpleFileData(bytes, name, contentType)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
