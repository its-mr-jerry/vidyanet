package com.kastack.vidyanet.data.repositories

import com.kastack.vidyanet.data.DatabaseManager
import com.kastack.vidyanet.models.role.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

interface RoleRepository {
    suspend fun getAllRoles(): Result<List<RoleDto>>
    suspend fun createRole(request: CreateRoleRequest): Result<RoleDto>
    suspend fun updateRole(id: Long, request: UpdateRoleRequest): Result<RoleDto>
    suspend fun deleteRole(id: Long): Result<Unit>
    suspend fun getRolePermissions(roleId: Long): Result<RolePermissionsDto>
    suspend fun updateRolePermissions(roleId: Long, permissions: RolePermissionsDto): Result<Unit>
    suspend fun getAllPermissions(): Result<List<ModulePermissionDto>>
}

class RoleRepositoryImpl(
    private val httpClient: HttpClient,
    private val databaseManager: DatabaseManager
) : RoleRepository {

    private fun HttpRequestBuilder.authHeader() {
        val token = databaseManager.getString("auth_token", "")
        if (token.isNotEmpty()) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    override suspend fun getAllRoles(): Result<List<RoleDto>> = runCatching {
        val response = httpClient.get("roles") { authHeader() }
        response.body()
    }

    override suspend fun createRole(request: CreateRoleRequest): Result<RoleDto> = runCatching {
        val response = httpClient.post("roles") {
            authHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        response.body()
    }

    override suspend fun updateRole(id: Long, request: UpdateRoleRequest): Result<RoleDto> = runCatching {
        val response = httpClient.put("roles/$id") {
            authHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        response.body()
    }

    override suspend fun deleteRole(id: Long): Result<Unit> = runCatching {
        val response = httpClient.delete("roles/$id") { authHeader() }
        if (response.status != HttpStatusCode.NoContent) throw Exception("Failed to delete role")
    }

    override suspend fun getRolePermissions(roleId: Long): Result<RolePermissionsDto> = runCatching {
        val response = httpClient.get("roles/$roleId/permissions") { authHeader() }
        response.body()
    }

    override suspend fun updateRolePermissions(roleId: Long, permissions: RolePermissionsDto): Result<Unit> = runCatching {
        val response = httpClient.put("roles/$roleId/permissions") {
            authHeader()
            contentType(ContentType.Application.Json)
            setBody(permissions)
        }
        if (response.status != HttpStatusCode.OK) throw Exception("Failed to update permissions")
    }

    override suspend fun getAllPermissions(): Result<List<ModulePermissionDto>> = runCatching {
        val response = httpClient.get("permissions") { authHeader() }
        response.body()
    }
}
