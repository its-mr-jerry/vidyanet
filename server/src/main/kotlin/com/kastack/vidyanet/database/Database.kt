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
            PermissionsTable,
            RolePermissionsTable,
            RolesTable,
            UsersTable,
            OtpsTable,
            UserRoleAssignmentsTable,
            SystemSettingsTable
        )
        
        seedRoles()
        seedPermissions()
        SchoolSeedScript.seedSchools()
    }
}

private fun seedPermissions() {
    if (PermissionsTable.selectAll().empty()) {
        val modules = listOf(
            "DASHBOARD", "ADMISSIONS", "STUDENTS", "PARENTS", "STAFF", 
            "ACADEMICS", "ATTENDANCE", "FINANCE", "LIBRARY", "TRANSPORT", 
            "INVENTORY", "COMMUNICATION", "REPORTS", "SETTINGS"
        )
        val actions = listOf("VIEW", "CREATE", "EDIT", "DELETE", "EXPORT")
        
        for (module in modules) {
            for (action in actions) {
                PermissionsTable.insert {
                    it[moduleName] = module
                    it[this.action] = action
                    it[description] = "Can $action $module"
                }
            }
        }
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
            Triple("ACCOUNTANT", "Accountant", "Access to financial records and fee management"),
            Triple("LIBRARIAN", "Librarian", "Manage library books and records"),
            Triple("TRANSPORT_MANAGER", "Transport Manager", "Manage vehicles and routes"),
            Triple("INVENTORY_MANAGER", "Inventory Manager", "Manage assets and stocks"),
            Triple("RECEPTIONIST", "Receptionist", "Front desk management"),
            Triple("CLERK", "Clerk", "General administrative tasks"),
            Triple("ADMISSION_OFFICER", "Admission Officer", "Manage student admissions and enquiries"),
            Triple("HR_MANAGER", "HR Manager", "Manage staff, payroll, and recruitment"),
            Triple("EXAM_CONTROLLER", "Exam Controller", "Manage examinations and results"),
            Triple("ACADEMIC_COORDINATOR", "Academic Coordinator", "Manage curriculum and timetable"),
            Triple("FINANCE_OFFICER", "Finance Officer", "Strategic financial planning and oversight")
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
