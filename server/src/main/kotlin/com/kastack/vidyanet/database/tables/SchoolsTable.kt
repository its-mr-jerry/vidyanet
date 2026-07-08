package com.kastack.vidyanet.database.tables

import com.kastack.vidyanet.database.toKotlinx
import com.kastack.vidyanet.models.schoolUser.SchoolStatus
import com.kastack.vidyanet.models.schoolUser.SchoolType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import kotlin.time.Clock

object SchoolsTable : LongIdTable("schools", "school_id") {

    val schoolCode = varchar("school_code", 20)
        .uniqueIndex()

    val schoolName = varchar("school_name", 150)

    val schoolType = enumerationByName(
        "school_type",
        30,
        SchoolType::class
    )

    val phone = varchar("phone", 20)

    val email = varchar("email", 150)
        .nullable()

    val website = varchar("website", 255)
        .nullable()

    val address = varchar("address", 255)

    val city = varchar("city", 100)

    val state = varchar("state", 100)

    val country = varchar("country", 100)

    val postalCode = varchar("postal_code", 20)

    val logoUrl = varchar("logo_url", 500)
        .nullable()

    val status = enumerationByName(
        "status",
        30,
        SchoolStatus::class
    )

    val createdAt = timestamp("created_at")
        .default(Clock.System.now().toKotlinx())

    val updatedAt = timestamp("updated_at")
        .default(Clock.System.now().toKotlinx())
}
