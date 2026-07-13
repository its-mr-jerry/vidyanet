package com.kastack.vidyanet.services.audit

import com.kastack.vidyanet.database.entities.AuditLogEntity
import com.kastack.vidyanet.database.entities.toDto
import com.kastack.vidyanet.database.tables.AuditLogsTable
import com.kastack.vidyanet.database.tables.SchoolsTable
import com.kastack.vidyanet.database.tables.UsersTable
import com.kastack.vidyanet.database.toKotlinx
import com.kastack.vidyanet.models.PagedResponse
import com.kastack.vidyanet.models.audit.AuditLogDto
import com.kastack.vidyanet.models.audit.AuditStatus
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.ceil
import kotlin.time.Clock

class AuditLogService {

    fun getAuditLogs(
        schoolId: Long?,
        search: String? = null,
        module: String? = null,
        status: AuditStatus? = null,
        page: Int = 1,
        pageSize: Int = 10
    ): PagedResponse<AuditLogDto> = transaction {
        val query = if (schoolId != null) {
            AuditLogsTable.selectAll().where { AuditLogsTable.schoolId eq schoolId }
        } else {
            AuditLogsTable.selectAll()
        }

        search?.let { s ->
            query.andWhere { (AuditLogsTable.action.lowerCase() like "%${s.lowercase()}%") or (AuditLogsTable.actionDetails.lowerCase() like "%${s.lowercase()}%") }
        }

        module?.let { m ->
            query.andWhere { AuditLogsTable.module eq m }
        }

        status?.let { s ->
            query.andWhere { AuditLogsTable.status eq s }
        }

        val totalItems = query.count()
        val totalPages = if (totalItems == 0L) 1 else ceil(totalItems.toDouble() / pageSize).toInt()

        val items = AuditLogEntity.wrapRows(query)
            .orderBy(AuditLogsTable.timestamp to SortOrder.DESC)
            .limit(pageSize)
            .offset(start = ((page - 1) * pageSize).toLong())
            .map { it.toDto() }

        PagedResponse(
            items = items,
            totalItems = totalItems,
            totalPages = totalPages,
            currentPage = page,
            pageSize = pageSize
        )
    }

    fun logAction(
        schoolId: Long?,
        userId: Long?,
        action: String,
        actionDetails: String? = null,
        module: String,
        status: AuditStatus,
        ipAddress: String? = null,
        userAgent: String? = null
    ) = transaction {
        AuditLogEntity.new {
            this.schoolId = schoolId?.let { EntityID(it, SchoolsTable) }
            this.userId = userId?.let { EntityID(it, UsersTable) }
            this.action = action
            this.actionDetails = actionDetails
            this.module = module
            this.status = status
            this.ipAddress = ipAddress
            this.userAgent = userAgent
            this.timestamp = Clock.System.now().toKotlinx()
        }
    }
}
