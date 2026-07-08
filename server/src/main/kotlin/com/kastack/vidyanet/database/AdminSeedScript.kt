package com.kastack.vidyanet.database

import com.kastack.vidyanet.database.tables.RolesTable
import com.kastack.vidyanet.database.tables.SchoolsTable
import com.kastack.vidyanet.database.tables.UserRoleAssignmentsTable
import com.kastack.vidyanet.database.tables.UsersTable
import com.kastack.vidyanet.models.user.UserStatus
import com.kastack.vidyanet.models.user.UserType
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Clock

/**
 * Run this script to seed the initial Platform Owner and a School Admin.
 */
fun main() {
    // 1. Initialize Database Connection
    configureDatabases()

    transaction {
        // --- PLATFORM OWNER SEEDING ---
        val platformOwnerPhone = "0000000000" // CHANGE THIS
        seedPlatformOwner(platformOwnerPhone)

    }
}

private fun seedPlatformOwner(phoneNum: String) {
    val existing = UsersTable.selectAll().where { UsersTable.phone eq phoneNum }.firstOrNull()
    if (existing == null) {
        println("Creating Platform Owner ($phoneNum)...")
        UsersTable.insert {
            it[phone] = phoneNum
            it[userType] = UserType.PLATFORM_OWNER
            it[status] = UserStatus.ACTIVE
            it[isPhoneVerified] = true
        }
        println("Platform Owner created successfully.")
    } else {
        println("Platform Owner with phone $phoneNum already exists.")
    }
}

private fun seedSchoolAdmin(phoneNum: String, schoolCode: String) {
    val school = SchoolsTable.selectAll().where { SchoolsTable.schoolCode eq schoolCode }.firstOrNull()
    
    if (school == null) {
        println("Error: Cannot create Admin. School with code '$schoolCode' not found. Please create the school via API first.")
        return
    }

    val schoolId = school[SchoolsTable.id]
    val existing = UsersTable.selectAll().where { UsersTable.phone eq phoneNum }.firstOrNull()

    if (existing == null) {
        println("Creating School Admin ($phoneNum) for school '$schoolCode'...")
        val newAdminId = UsersTable.insertAndGetId {
            it[phone] = phoneNum
            it[userType] = UserType.SCHOOL_USER
            it[status] = UserStatus.ACTIVE
            it[this.schoolId] = schoolId
            it[isPhoneVerified] = true
        }

        val adminRole = RolesTable.selectAll().where { RolesTable.roleCode eq "SCHOOL_ADMIN" }.firstOrNull()
        if (adminRole != null) {
            UserRoleAssignmentsTable.insert {
                it[userId] = newAdminId
                it[roleId] = adminRole[RolesTable.id]
                it[assignedAt] = Clock.System.now().toKotlinx()
            }
            println("School Admin created and role assigned.")
        } else {
            println("Error: SCHOOL_ADMIN role not found. Run DB seeding first.")
        }
    } else {
        println("User with phone $phoneNum already exists.")
    }
}
