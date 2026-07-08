package com.kastack.vidyanet.services.system


import com.kastack.vidyanet.database.tables.SystemSettingsTable
import com.kastack.vidyanet.models.system.SystemConfigDto
import com.kastack.vidyanet.models.system.UpdateSystemConfigRequest
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class SystemService {

    fun getSystemConfig(): SystemConfigDto = transaction {
        val row = SystemSettingsTable.selectAll().firstOrNull() ?: SystemSettingsTable.insert {
            it[isMaintenanceMode] = false
        }.resultedValues!!.first()

        SystemConfigDto(
            isMaintenanceMode = row[SystemSettingsTable.isMaintenanceMode],
            supportPhone = row[SystemSettingsTable.supportPhone],
            supportEmail = row[SystemSettingsTable.supportEmail],
            appVersion = row[SystemSettingsTable.appVersion],
            allowNewSchoolRegistration = row[SystemSettingsTable.allowNewSchoolRegistration]
        )
    }

    fun updateSystemConfig(request: UpdateSystemConfigRequest): SystemConfigDto = transaction {
        SystemSettingsTable.update {
            request.isMaintenanceMode?.let { mode -> it[isMaintenanceMode] = mode }
            request.supportPhone?.let { phone -> it[supportPhone] = phone }
            request.supportEmail?.let { email -> it[supportEmail] = email }
            request.appVersion?.let { version -> it[appVersion] = version }
            request.allowNewSchoolRegistration?.let { allow -> it[allowNewSchoolRegistration] = allow }
        }
        getSystemConfig()
    }
}
