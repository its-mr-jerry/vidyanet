package com.kastack.vidyanet.services.user


import com.kastack.vidyanet.database.entities.UserEntity
import com.kastack.vidyanet.database.entities.toDto
import com.kastack.vidyanet.database.tables.SchoolsTable
import com.kastack.vidyanet.database.tables.UsersTable
import com.kastack.vidyanet.models.PagedResponse
import com.kastack.vidyanet.models.user.UpdateUserRequest
import com.kastack.vidyanet.models.user.UserDto
import com.kastack.vidyanet.models.user.UserStatsDto
import com.kastack.vidyanet.models.user.UserStatus
import com.kastack.vidyanet.models.user.UserType
import com.kastack.vidyanet.plugins.UnauthorizedException
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayIn
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Clock as StdlibClock
import kotlinx.datetime.toDeprecatedInstant
import kotlin.math.ceil

class UserService {

    fun getAllUsers(
        search: String? = null,
        userType: UserType? = null,
        status: UserStatus? = null,
        page: Int = 1,
        pageSize: Int = 10
    ): PagedResponse<UserDto> = transaction {
        val query = (UsersTable leftJoin SchoolsTable).selectAll()

        search?.let { s ->
            val searchLower = s.lowercase()
            query.andWhere {
                (UsersTable.phone like "%$s%") or
                        (SchoolsTable.schoolName like "%$searchLower%")
            }
        }

        userType?.let { type ->
            query.andWhere { UsersTable.userType eq type }
        }

        status?.let { s ->
            query.andWhere { UsersTable.status eq s }
        }

        val totalItems = query.count()
        val totalPages = if (totalItems == 0L) 1 else ceil(totalItems.toDouble() / pageSize).toInt()

        val items = UserEntity.wrapRows(query)
            .limit(pageSize)
            .offset(start = ((page - 1) * pageSize).toLong()).map { it.toDto() }

        PagedResponse(
            items = items,
            totalItems = totalItems,
            totalPages = totalPages,
            currentPage = page,
            pageSize = pageSize
        )
    }

    fun getUserStats(): UserStatsDto = transaction {
        val total = UsersTable.selectAll().count()

        val today = StdlibClock.System.todayIn(TimeZone.currentSystemDefault())
        val startOfToday = today.atStartOfDayIn(TimeZone.currentSystemDefault())

        val newToday = UsersTable.selectAll().where {
            UsersTable.createdAt greaterEq startOfToday.toDeprecatedInstant()
        }.count()

        val byType = UserType.entries.associate { type ->
            type.name to UsersTable.selectAll().where { UsersTable.userType eq type }.count()
        }

        UserStatsDto(
            totalUsers = total,
            newUsersToday = newToday,
            usersByType = byType
        )
    }

    fun getUser(id: Long): UserDto {
        val user = transaction {
            UserEntity.findById(id)
        } ?: throw UnauthorizedException("User not found")

        return user.toDto()
    }

    fun updateUser(id: Long, request: UpdateUserRequest): UserDto {
        val user = transaction {
            UserEntity.findById(id)?.apply {
                request.userType?.let { userType = it }
                request.status?.let { status = it }
                request.schoolId?.let { 
                    schoolId = org.jetbrains.exposed.dao.id.EntityID(it, com.kastack.vidyanet.database.tables.SchoolsTable)
                }
                updatedAt = StdlibClock.System.now().toDeprecatedInstant()
            }
        } ?: throw UnauthorizedException("User not found")

        return user.toDto()
    }

    fun deleteUser(id: Long) {
        transaction {
            UserEntity.findById(id)?.delete() ?: throw UnauthorizedException("User not found")
        }
    }

    fun getMe(userId: Long): UserDto = transaction {
        UserEntity.findById(userId)?.toDto() ?: throw UnauthorizedException("User not found")
    }
}
