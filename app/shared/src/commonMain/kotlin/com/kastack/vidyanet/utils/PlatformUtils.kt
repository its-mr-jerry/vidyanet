package com.kastack.vidyanet.utils

interface FileData {
    val bytes: ByteArray
    val name: String
    val contentType: String
}

class SimpleFileData(
    override val bytes: ByteArray,
    override val name: String,
    override val contentType: String
) : FileData

expect class PlatformUtils() {
    fun readFileData(path: String): FileData?
}
