package com.kastack.vidyanet.database.tables

import com.kastack.vidyanet.database.toKotlinx
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import kotlin.time.Clock

object UserRoleAssignmentsTable : LongIdTable(
    "user_role_assignments",
    "assignment_id"
) {

    val userId = reference(
        "user_id",
        UsersTable
    )

    val roleId = reference(
        "role_id",
        RolesTable
    )

    val assignedBy = reference(
        "assigned_by",
        UsersTable
    ).nullable()

    val assignedAt = timestamp("assigned_at")
        .default(Clock.System.now().toKotlinx())
}
