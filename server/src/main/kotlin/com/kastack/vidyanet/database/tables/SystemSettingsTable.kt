package com.kastack.vidyanet.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object SystemSettingsTable : IntIdTable("system_settings") {
    val isMaintenanceMode = bool("is_maintenance_mode").default(false)
    val supportPhone = varchar("support_phone", 20).default("1234567890")
    val supportEmail = varchar("support_email", 100).default("support@kastack.com")
    val appVersion = varchar("app_version", 10).default("1.0.0")
    val allowNewSchoolRegistration = bool("allow_new_school_registration").default(true)
}
