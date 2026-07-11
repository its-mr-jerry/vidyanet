package com.kastack.vidyanet.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object SchoolSettingsTable : LongIdTable("school_settings", "id") {
    val schoolId = reference("school_id", SchoolsTable, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val registrationNumber = varchar("registration_number", 50).nullable()
    val motto = varchar("motto", 255).nullable()
    val establishmentDate = varchar("establishment_date", 20).nullable()
    val affiliationBoard = varchar("affiliation_board", 100).nullable()
    val primaryBrandColor = varchar("primary_brand_color", 10).default("#4F46E5")
    val isMaintenanceMode = bool("is_maintenance_mode").default(false)
}
