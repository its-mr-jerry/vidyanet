package com.kastack.vidyanet.database


import com.kastack.vidyanet.config.AppConfig
import com.kastack.vidyanet.database.tables.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun configureDatabases(isTest: Boolean = false) {
    if (isTest) return

    val dbname = AppConfig.dbName
    val user = AppConfig.dbUser
    val password = AppConfig.dbPassword
    val host = AppConfig.dbHost
    val port = AppConfig.dbPort

    val config = HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = "jdbc:postgresql://$host:$port/$dbname"
        username = user
        this.password = password
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }

    val dataSource = HikariDataSource(config)

    // Flyway migration: This is the "right way" to handle schema updates
    Flyway.configure()
        .dataSource(dataSource)
        .baselineOnMigrate(true)
        .load()
        .migrate()

    val database = Database.connect(dataSource)

    transaction(database) {

        SchemaUtils.create(
            SchoolsTable,
            SchoolSettingsTable,
            SchoolWorkingHoursTable,
            SchoolBranchesTable,
            AcademicSettingsTable,
            AcademicSessionsTable,
            HolidaysTable,
            RolesTable,
            UsersTable,
            OtpsTable,
            UserRoleAssignmentsTable,
            SystemSettingsTable
        )
        
        seedRoles()
        SchoolSeedScript.seedSchools()
    }
}

private fun seedRoles() {
    if (RolesTable.selectAll().empty()) {
        val defaultRoles = listOf(
            Triple("SCHOOL_ADMIN", "School Admin", "Full access to school data and settings"),
            Triple("PRINCIPAL", "Principal", "Academic and administrative head of the school"),
            Triple("TEACHER", "Teacher", "Access to classroom management and student data"),
            Triple("STUDENT", "Student", "Access to learning materials and own records"),
            Triple("PARENT", "Parent", "Access to their children's records"),
            Triple("ACCOUNTANT", "Accountant", "Access to financial records and fee management")
        )
        
        for ((code, name, desc) in defaultRoles) {
            RolesTable.insert {
                it[roleCode] = code
                it[roleName] = name
                it[description] = desc
                it[isSystemRole] = true
            }
        }
    }
}
