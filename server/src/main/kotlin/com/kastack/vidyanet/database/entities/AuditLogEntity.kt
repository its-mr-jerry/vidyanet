package com.kastack.vidyanet.database.entities

import com.kastack.vidyanet.database.tables.AuditLogsTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AuditLogEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<AuditLogEntity>(AuditLogsTable)

    var schoolId: EntityID<Long>? by AuditLogsTable.schoolId
    var timestamp by AuditLogsTable.timestamp
    var userId: EntityID<Long>? by AuditLogsTable.userId
    var action by AuditLogsTable.action
    var actionDetails by AuditLogsTable.actionDetails
    var module by AuditLogsTable.module
    var status by AuditLogsTable.status
    var ipAddress by AuditLogsTable.ipAddress
    var userAgent by AuditLogsTable.userAgent

    val user by UserEntity optionalReferencedOn AuditLogsTable.userId
}
