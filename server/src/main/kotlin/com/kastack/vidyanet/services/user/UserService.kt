package com.kastack.vidyanet.services.user


import com.kastack.vidyanet.database.entities.*
import com.kastack.vidyanet.database.tables.*
import com.kastack.vidyanet.models.PagedResponse
import com.kastack.vidyanet.models.user.*
import com.kastack.vidyanet.database.toKotlinx
import com.kastack.vidyanet.plugins.UnauthorizedException
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayIn
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Clock as StdlibClock
import kotlin.math.ceil

class UserService {

    fun getAllUsers(
        search: String? = null,
        userType: UserType? = null,
        status: UserStatus? = null,
        schoolId: Long? = null,
        page: Int = 1,
        pageSize: Int = 10
    ): PagedResponse<UserDto> = transaction {
        val query = UsersTable.selectAll()

        search?.let { s ->
            val searchLower = "%${s.lowercase()}%"
            query.andWhere {
                (UsersTable.phone like searchLower) or
                        (UsersTable.fullName.lowerCase() like searchLower) or
                        (UsersTable.email.lowerCase() like searchLower)
            }
        }

        userType?.let { type ->
            query.andWhere { UsersTable.userType eq type }
        }

        status?.let { s ->
            query.andWhere { UsersTable.status eq s }
        }
        
        schoolId?.let { sid ->
            query.andWhere { UsersTable.schoolId eq sid }
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

    fun createUser(request: CreateUserRequest): UserDto = transaction {
        val user = UserEntity.new {
            phone = request.phone
            fullName = request.fullName
            email = request.email
            userType = request.userType
            status = UserStatus.ACTIVE
            schoolId = request.schoolId?.let { EntityID(it, SchoolsTable) }
            createdAt = StdlibClock.System.now().toKotlinx()
            updatedAt = StdlibClock.System.now().toKotlinx()
        }

        request.roleIds.forEach { rid ->
            UserRoleAssignmentEntity.new {
                userId = user.id
                roleId = EntityID(rid, RolesTable)
                assignedAt = StdlibClock.System.now().toKotlinx()
            }
        }

        user.toDto()
    }

    fun getUserStats(): UserStatsDto = transaction {
        val total = UsersTable.selectAll().count()

        val today = StdlibClock.System.todayIn(TimeZone.currentSystemDefault())
        val startOfToday = today.atStartOfDayIn(TimeZone.currentSystemDefault())

        val newToday = UsersTable.selectAll().where {
            UsersTable.createdAt greaterEq startOfToday.toKotlinx()
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
            val userEntity = UserEntity.findById(id) ?: return@transaction null
            userEntity.apply {
                request.fullName?.let { fullName = it }
                request.email?.let { email = it }
                request.userType?.let { userType = it }
                request.status?.let { status = it }
                request.schoolId?.let { 
                    schoolId = EntityID(it, SchoolsTable)
                }
                updatedAt = StdlibClock.System.now().toKotlinx()
            }
            
            request.roleIds?.let { rids ->
                // Simple role replacement: delete old, add new
                UserRoleAssignmentsTable.deleteWhere { userId eq id }
                rids.forEach { rid ->
                    UserRoleAssignmentEntity.new {
                        userId = EntityID(id, UsersTable)
                        roleId = EntityID(rid, RolesTable)
                        assignedAt = StdlibClock.System.now().toKotlinx()
                    }
                }
            }
            userEntity
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

