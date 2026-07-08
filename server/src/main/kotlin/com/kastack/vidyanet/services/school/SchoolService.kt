package com.kastack.vidyanet.services.school

import com.kastack.vidyanet.database.entities.SchoolEntity
import com.kastack.vidyanet.database.entities.toDto
import com.kastack.vidyanet.database.tables.SchoolsTable
import com.kastack.vidyanet.database.toKotlinx
import com.kastack.vidyanet.models.schoolUser.CreateSchoolRequest
import com.kastack.vidyanet.models.schoolUser.SchoolDto
import com.kastack.vidyanet.models.schoolUser.UpdateSchoolRequest
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Clock

class SchoolService {

    fun createSchool(request: CreateSchoolRequest): SchoolDto = transaction {
        SchoolEntity.new {
            schoolCode = request.schoolCode
            schoolName = request.schoolName
            schoolType = request.schoolType
            phone = request.phone
            email = request.email
            website = request.website
            address = request.address
            city = request.city
            state = request.state
            country = request.country
            postalCode = request.postalCode
            logoUrl = request.logoUrl
            status = request.status
            createdAt = Clock.System.now().toKotlinx()
            updatedAt = Clock.System.now().toKotlinx()
        }.toDto()
    }

    fun getAllSchools(): List<SchoolDto> = transaction {
        SchoolEntity.all().orderBy(SchoolsTable.schoolName to SortOrder.ASC).map { it.toDto() }
    }

    fun getSchoolById(id: Long): SchoolDto? = transaction {
        SchoolEntity.findById(id)?.toDto()
    }

    fun getSchoolByCode(code: String): SchoolDto? = transaction {
        SchoolEntity.find { SchoolsTable.schoolCode eq code }.firstOrNull()?.toDto()
    }

    fun updateSchool(id: Long, request: UpdateSchoolRequest): SchoolDto = transaction {
        val school = SchoolEntity.findById(id) ?: throw IllegalArgumentException("School not found")
        
        request.schoolName?.let { school.schoolName = it }
        request.schoolType?.let { school.schoolType = it }
        request.phone?.let { school.phone = it }
        request.email?.let { school.email = it }
        request.website?.let { school.website = it }
        request.address?.let { school.address = it }
        request.city?.let { school.city = it }
        request.state?.let { school.state = it }
        request.country?.let { school.country = it }
        request.postalCode?.let { school.postalCode = it }
        request.logoUrl?.let { school.logoUrl = it }
        request.status?.let { school.status = it }
        
        school.updatedAt = Clock.System.now().toKotlinx()
        
        school.toDto()
    }

    fun deleteSchool(id: Long): Boolean = transaction {
        val school = SchoolEntity.findById(id)
        if (school != null) {
            school.delete()
            true
        } else {
            false
        }
    }
}
