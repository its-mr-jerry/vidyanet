package com.kastack.vidyanet.data.repositories

import com.kastack.vidyanet.data.DatabaseManager
import com.kastack.vidyanet.models.schoolUser.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

interface SchoolRepository {
    suspend fun getAllSchools(): Result<List<SchoolDto>>
    suspend fun getSchoolById(id: Long): Result<SchoolDto>
    suspend fun createSchool(request: CreateSchoolRequest): Result<SchoolDto>
    suspend fun updateSchool(id: Long, request: UpdateSchoolRequest): Result<SchoolDto>
    suspend fun deleteSchool(id: Long): Result<Unit>
    suspend fun getSchoolSettings(schoolId: Long): Result<SchoolSettingsDto>
    suspend fun updateSchoolSettings(schoolId: Long, request: UpdateSchoolSettingsRequest): Result<SchoolSettingsDto>
}

class SchoolRepositoryImpl(
    private val httpClient: HttpClient,
    private val databaseManager: DatabaseManager
) : SchoolRepository {

    private fun HttpRequestBuilder.authHeader() {
        val token = databaseManager.getString("auth_token", "")
        if (token.isNotEmpty()) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    override suspend fun getAllSchools(): Result<List<SchoolDto>> = runCatching {
        val response = httpClient.get("schools") {
            authHeader()
        }
        if (response.status == HttpStatusCode.OK) {
            response.body<List<SchoolDto>>()
        } else {
            throw Exception("Failed to fetch schools: ${response.status}")
        }
    }

    override suspend fun getSchoolById(id: Long): Result<SchoolDto> = runCatching {
        val response = httpClient.get("schools/$id") {
            authHeader()
        }
        if (response.status == HttpStatusCode.OK) {
            response.body<SchoolDto>()
        } else {
            throw Exception("Failed to fetch school: ${response.status}")
        }
    }

    override suspend fun createSchool(request: CreateSchoolRequest): Result<SchoolDto> = runCatching {
        val response = httpClient.post("schools") {
            authHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status == HttpStatusCode.Created) {
            response.body<SchoolDto>()
        } else {
            throw Exception("Failed to create school: ${response.status}")
        }
    }

    override suspend fun updateSchool(id: Long, request: UpdateSchoolRequest): Result<SchoolDto> = runCatching {
        val response = httpClient.put("schools/$id") {
            authHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status == HttpStatusCode.OK) {
            response.body<SchoolDto>()
        } else {
            throw Exception("Failed to update school: ${response.status}")
        }
    }

    override suspend fun deleteSchool(id: Long): Result<Unit> = runCatching {
        val response = httpClient.delete("schools/$id") {
            authHeader()
        }
        if (response.status != HttpStatusCode.NoContent) {
            throw Exception("Failed to delete school: ${response.status}")
        }
    }

    override suspend fun getSchoolSettings(schoolId: Long): Result<SchoolSettingsDto> = runCatching {
        val response = httpClient.get("schools/$schoolId/settings") {
            authHeader()
        }
        if (response.status == HttpStatusCode.OK) {
            response.body<SchoolSettingsDto>()
        } else {
            throw Exception("Failed to fetch settings: ${response.status}")
        }
    }

    override suspend fun updateSchoolSettings(schoolId: Long, request: UpdateSchoolSettingsRequest): Result<SchoolSettingsDto> = runCatching {
        val response = httpClient.put("schools/$schoolId/settings") {
            authHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status == HttpStatusCode.OK) {
            response.body<SchoolSettingsDto>()
        } else {
            val errorBody = response.body<Map<String, String>>()
            throw Exception(errorBody["message"] ?: "Failed to update settings: ${response.status}")
        }
    }
}
