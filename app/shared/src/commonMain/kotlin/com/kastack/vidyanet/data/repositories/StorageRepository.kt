package com.kastack.vidyanet.data.repositories


import com.kastack.vidyanet.data.DatabaseManager
import com.kastack.vidyanet.models.storage.PresignedUrlRequest
import com.kastack.vidyanet.models.storage.PresignedUrlResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

interface StorageRepository {
    suspend fun getPresignedUrl(fileName: String, contentType: String, folder: String, fileSize: Long? = null): Result<PresignedUrlResponse>
    suspend fun uploadFile(url: String, fileBytes: ByteArray, contentType: String): Result<Unit>
}

class StorageRepositoryImpl(
    private val httpClient: HttpClient,
    private val databaseManager: DatabaseManager
) : StorageRepository {

    private fun HttpRequestBuilder.authHeader() {
        val token = databaseManager.getString("auth_token", "")
        if (token.isNotEmpty()) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    override suspend fun getPresignedUrl(fileName: String, contentType: String, folder: String, fileSize: Long?): Result<PresignedUrlResponse> = runCatching {
        val response = httpClient.post("storage/presigned-url") {
            authHeader()
            contentType(ContentType.Application.Json)
            setBody(PresignedUrlRequest(fileName, contentType, folder, fileSize))
        }
        if (!response.status.isSuccess()) {
            val errorBody = try { response.body<Map<String, String>>()["message"] } catch (e: Exception) { null }
            throw Exception(errorBody ?: "Server error: ${response.status}")
        }
        response.body()
    }

    override suspend fun uploadFile(url: String, fileBytes: ByteArray, contentType: String): Result<Unit> = runCatching {
        val response = httpClient.put(url) {
            header(HttpHeaders.ContentType, contentType)
            setBody(fileBytes)
        }
        if (!response.status.isSuccess()) {
            throw Exception("Upload failed with status: ${response.status}")
        }
    }
}
