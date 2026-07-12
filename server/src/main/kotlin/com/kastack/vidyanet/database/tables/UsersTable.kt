package com.kastack.vidyanet.database.tables

import com.kastack.vidyanet.database.toKotlinx
import com.kastack.vidyanet.models.user.UserStatus
import com.kastack.vidyanet.models.user.UserType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import kotlin.time.Clock

object UsersTable : LongIdTable("users", "user_id") {

    val phone = varchar("phone", 20).uniqueIndex()

    val fullName = varchar("full_name", 100).nullable()

    val email = varchar("email", 150).nullable()

    val userType = enumerationByName(
        "user_type",
        20,
        UserType::class
    )

    val schoolId = reference(
        "school_id",
        SchoolsTable
    ).nullable()

    val status = enumerationByName(
        "status",
        30,
        UserStatus::class
    )

    val isPhoneVerified = bool("is_phone_verified")
        .default(false)

    val lastLoginAt = timestamp("last_login_at")
        .nullable()

    val createdAt = timestamp("created_at")
        .default(Clock.System.now().toKotlinx())

    val updatedAt = timestamp("updated_at")
        .default(Clock.System.now().toKotlinx())

    val deletedAt = timestamp("deleted_at")
        .nullable()
}
