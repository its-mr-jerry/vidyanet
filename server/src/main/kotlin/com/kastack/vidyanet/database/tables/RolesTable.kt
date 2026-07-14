package com.kastack.vidyanet.database.tables

import com.kastack.vidyanet.database.toKotlinx
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import kotlin.time.Clock

object RolesTable : LongIdTable("roles", "role_id") {

    val roleCode = varchar("role_code", 50)

    val roleName = varchar("role_name", 100)

    val description = varchar("description", 255)
        .nullable()

    val schoolId = reference(
        "school_id",
        SchoolsTable
    ).nullable()

    val isSystemRole = bool("is_system_role")
        .default(true)

    val createdAt = timestamp("created_at")
        .default(Clock.System.now().toKotlinx())

    val updatedAt = timestamp("updated_at")
        .default(Clock.System.now().toKotlinx())
}
