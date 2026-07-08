package com.kastack.vidyanet.data.repositories

import com.kastack.vidyanet.data.DatabaseManager
import com.kastack.vidyanet.models.PagedResponse
import com.kastack.vidyanet.models.user.UpdateUserRequest
import com.kastack.vidyanet.models.user.UserDto
import com.kastack.vidyanet.models.user.UserStatsDto
import com.kastack.vidyanet.models.user.UserStatus
import com.kastack.vidyanet.models.user.UserType
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

interface UserRepository {
    suspend fun getAllUsers(
        search: String? = null,
        userType: UserType? = null,
        status: UserStatus? = null,
        page: Int = 1,
        pageSize: Int = 10
    ): Result<PagedResponse<UserDto>>

    suspend fun updateUser(id: Long, request: UpdateUserRequest): Result<UserDto>

    suspend fun deleteUser(id: Long): Result<Unit>

    suspend fun getUserById(id: Long): Result<UserDto>

    suspend fun getUserStats(): Result<UserStatsDto>
    suspend fun getMe(): Result<UserDto>

}

class UserRepositoryImpl(
    private val httpClient: HttpClient,
    private val databaseManager: DatabaseManager
) : UserRepository {

    private fun HttpRequestBuilder.authHeader() {
        val token = databaseManager.getString("auth_token", "")
        if (token.isNotEmpty()) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    override suspend fun getAllUsers(
        search: String?,
        userType: UserType?,
        status: UserStatus?,
        page: Int,
        pageSize: Int
    ): Result<PagedResponse<UserDto>> = runCatching {
        val response = httpClient.get("users") {
            authHeader()
            search?.let { parameter("search", it) }
            userType?.let { parameter("userType", it.name) }
            status?.let { parameter("status", it.name) }
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
        if (response.status == HttpStatusCode.OK) {
            response.body<PagedResponse<UserDto>>()
        } else {
            val errorMsg = try { response.body<Map<String, String>>()["message"] } catch(_: Exception) { null }
            throw Exception(errorMsg ?: "Server returned ${response.status}")
        }
    }

    override suspend fun updateUser(id: Long, request: UpdateUserRequest): Result<UserDto> = runCatching {
        val response = httpClient.put("users/$id") {
            authHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (response.status == HttpStatusCode.OK) {
            response.body<UserDto>()
        } else {
            val errorMsg = try { response.body<Map<String, String>>()["message"] } catch(_: Exception) { null }
            throw Exception(errorMsg ?: "Failed to update user: ${response.status}")
        }
    }

    override suspend fun deleteUser(id: Long): Result<Unit> = runCatching {
        val response = httpClient.delete("users/$id") {
            authHeader()
        }
        if (response.status != HttpStatusCode.NoContent) {
            val errorMsg = try { response.body<Map<String, String>>()["message"] } catch(_: Exception) { null }
            throw Exception(errorMsg ?: "Failed to delete user: ${response.status}")
        }
    }

    override suspend fun getUserById(id: Long): Result<UserDto> = runCatching {
        val response = httpClient.get("users/$id") {
            authHeader()
        }
        if (response.status == HttpStatusCode.OK) {
            response.body<UserDto>()
        } else {
            val errorMsg = try { response.body<Map<String, String>>()["message"] } catch(_: Exception) { null }
            throw Exception(errorMsg ?: "Failed to get user: ${response.status}")
        }
    }

    override suspend fun getUserStats(): Result<UserStatsDto> = runCatching {
        val response = httpClient.get("users/stats") {
            authHeader()
        }
        if (response.status == HttpStatusCode.OK) {
            response.body<UserStatsDto>()
        } else {
            val errorMsg = try { response.body<Map<String, String>>()["message"] } catch(_: Exception) { null }
            throw Exception(errorMsg ?: "Failed to get stats: ${response.status}")
        }
    }

    override suspend fun getMe(): Result<UserDto> = runCatching {
        val response = httpClient.get("users/me") {
            authHeader()
        }
        if (response.status == HttpStatusCode.OK) {
            response.body<UserDto>()
        } else {
            throw Exception("Failed to fetch user info: ${response.status}")
        }
    }
}
