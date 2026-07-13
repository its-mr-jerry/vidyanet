package com.kastack.vidyanet.data.repositories

import com.kastack.vidyanet.data.DatabaseManager
import com.kastack.vidyanet.models.schoolUser.*
import com.kastack.vidyanet.models.audit.AuditLogDto
import com.kastack.vidyanet.models.audit.AuditStatus
import com.kastack.vidyanet.models.PagedResponse
import com.kastack.vidyanet.models.settings.NotificationSettingsDto
import com.kastack.vidyanet.models.settings.UpdateNotificationSettingsRequest
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
    suspend fun getAcademicSettings(schoolId: Long): Result<AcademicSettingsDto>
    suspend fun updateAcademicSettings(schoolId: Long, request: UpdateAcademicSettingsRequest): Result<Unit>
    suspend fun addAcademicSession(schoolId: Long, session: AcademicSessionDto): Result<Unit>
    suspend fun deleteAcademicSession(schoolId: Long, sessionId: Long): Result<Unit>
    suspend fun addHoliday(schoolId: Long, holiday: HolidayDto): Result<Unit>
    suspend fun deleteHoliday(schoolId: Long, holidayId: Long): Result<Unit>
    suspend fun getNotificationSettings(schoolId: Long): Result<NotificationSettingsDto>
    suspend fun updateNotificationSettings(schoolId: Long, request: UpdateNotificationSettingsRequest): Result<Unit>
    suspend fun getAuditLogs(
        schoolId: Long,
        search: String? = null,
        module: String? = null,
        status: AuditStatus? = null,
        page: Int = 1,
        pageSize: Int = 10
    ): Result<PagedResponse<AuditLogDto>>
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

    override suspend fun getAcademicSettings(schoolId: Long): Result<AcademicSettingsDto> = runCatching {
        val response = httpClient.get("schools/$schoolId/academic-settings") {
            authHeader()
        }
        if (response.status == HttpStatusCode.OK) {
            response.body<AcademicSettingsDto>()
        } else {
            throw Exception("Failed to fetch academic settings: ${response.status}")
        }
    }

    override suspend fun updateAcademicSettings(schoolId: Long, request: UpdateAcademicSettingsRequest): Result<Unit> = runCatching {
        val response = httpClient.put("schools/$schoolId/academic-settings") {
            authHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Failed to update academic settings: ${response.status}")
        }
    }

    override suspend fun addAcademicSession(schoolId: Long, session: AcademicSessionDto): Result<Unit> = runCatching {
        val response = httpClient.post("schools/$schoolId/academic-settings/sessions") {
            authHeader()
            contentType(ContentType.Application.Json)
            setBody(session)
        }
        if (response.status != HttpStatusCode.Created) {
            throw Exception("Failed to add session: ${response.status}")
        }
    }

    override suspend fun deleteAcademicSession(schoolId: Long, sessionId: Long): Result<Unit> = runCatching {
        val response = httpClient.delete("schools/$schoolId/academic-settings/sessions/$sessionId") {
            authHeader()
        }
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Failed to delete session: ${response.status}")
        }
    }

    override suspend fun addHoliday(schoolId: Long, holiday: HolidayDto): Result<Unit> = runCatching {
        val response = httpClient.post("schools/$schoolId/academic-settings/holidays") {
            authHeader()
            contentType(ContentType.Application.Json)
            setBody(holiday)
        }
        if (response.status != HttpStatusCode.Created) {
            throw Exception("Failed to add holiday: ${response.status}")
        }
    }

    override suspend fun deleteHoliday(schoolId: Long, holidayId: Long): Result<Unit> = runCatching {
        val response = httpClient.delete("schools/$schoolId/academic-settings/holidays/$holidayId") {
            authHeader()
        }
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Failed to delete holiday: ${response.status}")
        }
    }

    override suspend fun getNotificationSettings(schoolId: Long): Result<NotificationSettingsDto> = runCatching {
        val response = httpClient.get("schools/$schoolId/notification-settings") {
            authHeader()
        }
        if (response.status == HttpStatusCode.OK) {
            response.body<NotificationSettingsDto>()
        } else {
            throw Exception("Failed to fetch notification settings: ${response.status}")
        }
    }

    override suspend fun updateNotificationSettings(schoolId: Long, request: UpdateNotificationSettingsRequest): Result<Unit> = runCatching {
        val response = httpClient.put("schools/$schoolId/notification-settings") {
            authHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Failed to update notification settings: ${response.status}")
        }
    }

    override suspend fun getAuditLogs(
        schoolId: Long,
        search: String?,
        module: String?,
        status: AuditStatus?,
        page: Int,
        pageSize: Int
    ): Result<PagedResponse<AuditLogDto>> = runCatching {
        val response = httpClient.get("schools/$schoolId/audit-logs") {
            authHeader()
            search?.let { parameter("search", it) }
            module?.let { parameter("module", it) }
            status?.let { parameter("status", it.name) }
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
        if (response.status == HttpStatusCode.OK) {
            response.body<PagedResponse<AuditLogDto>>()
        } else {
            throw Exception("Failed to fetch audit logs: ${response.status}")
        }
    }
}
