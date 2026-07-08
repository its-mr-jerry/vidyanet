package com.kastack.vidyanet.models.storage

import kotlinx.serialization.Serializable

@Serializable
data class PresignedUrlResponse(
    val uploadUrl: String,
    val publicUrl: String,
    val key: String
)

@Serializable
data class PresignedUrlRequest(
    val fileName: String,
    val contentType: String,
    val folder: String = "general",
    val fileSize: Long? = null
)
