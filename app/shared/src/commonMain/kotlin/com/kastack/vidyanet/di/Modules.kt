package com.kastack.vidyanet.di

import com.kastack.vidyanet.core.GlobalStore
import com.kastack.vidyanet.network.createHttpClient
import com.kastack.vidyanet.data.DatabaseManager
import com.kastack.vidyanet.data.repositories.*
import com.kastack.vidyanet.commonUi.viewModels.LoginViewModel
import com.kastack.vidyanet.commonUi.viewModels.SplashViewModel
import com.kastack.vidyanet.school.viewModels.*
import com.kastack.vidyanet.superAdmin.viewModels.SchoolsViewModel
import com.kastack.vidyanet.superAdmin.viewModels.SuperAdminDashboardViewModel
import com.kastack.vidyanet.utils.PlatformUtils
import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Core infrastructure module - needed by everyone
 */
val coreModule = module {
    single { Settings() }
    single { createHttpClient() }
    singleOf(::DatabaseManager)
    singleOf(::GlobalStore)
    single<StorageRepository> { StorageRepositoryImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<SchoolRepository> { SchoolRepositoryImpl(get(), get()) }

    single { PlatformUtils() }
}

/**
 * Authentication and shared UI module
 */
val authModule = module {
    viewModelOf(::SplashViewModel)
    viewModelOf(::LoginViewModel)
}

/**
 * Super Admin specific module - contains repositories and viewmodels only for Admin
 */
val adminModule = module {
    viewModelOf(::SuperAdminDashboardViewModel)
    viewModelOf(::SchoolsViewModel)
}



/**
 * School specific module
 */
val schoolModule = module {
    viewModelOf(::SchoolAppViewModel)
    viewModelOf(::SchoolDashboardViewModel)
    viewModelOf(::SchoolSettingsViewModel)
    viewModelOf(::AcademicSettingsViewModel)
    viewModelOf(::RolesPermissionsViewModel)
    viewModelOf(::UserManagementViewModel)
    viewModelOf(::AuditLogsViewModel)
    viewModelOf(::NotificationSettingsViewModel)
    viewModelOf(::BackupRestoreViewModel)
    viewModelOf(::IntegrationsViewModel)
}

/**
 * User specific module
 */
val userModule = module {

}

// Combined modules list
val appModules = listOf(
    coreModule,
    authModule,
    adminModule,
    schoolModule,
//    merchantModule,
//    riderModule,
    userModule
)
