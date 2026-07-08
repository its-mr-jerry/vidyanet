package com.kastack.vidyanet.theme

import androidx.compose.ui.graphics.Color

// Academic Precision Palette -In House ERP

// --- LIGHT THEME COLORS ---
val LightSurface = Color(0xFFFBF8FF)
val LightSurfaceContainer = Color(0xFFF4F2FC)
val LightPrimaryBrand = Color(0xFF1E40AF)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightOutline = Color(0xFF767680)

// --- DARK THEME COLORS ---
val DarkSurface = Color(0xFF1A1B23)
val DarkSurfaceContainer = Color(0xFF24252D)
val DarkPrimaryBrand = Color(0xFFDBE1FF)
val DarkOnSurface = Color(0xFFE4E1E9)
val DarkOutline = Color(0xFF90909A)

// --- SEMANTIC COLORS ---
val AcademicSuccess = Color(0xFF15803D)
val AcademicWarning = Color(0xFFB45309)
val AcademicError = Color(0xFFB91C1C)

// Legacy / Support Mapping (To keep existing code working)
val InstitutionalBlue = LightPrimaryBrand
val OnInstitutionalBlue = LightOnPrimary
val AcademicSurface = LightSurface
val AcademicSurfaceContainer = LightSurfaceContainer
val AcademicSurfaceBright = LightSurface
val AcademicOnSurface = Color(0xFF1A1B22)

val Primary = LightPrimaryBrand
val OnPrimary = LightOnPrimary
val Background = LightSurface
val OnBackground = AcademicOnSurface
val Surface = LightSurface
val OnSurface = AcademicOnSurface
val SurfaceVariant = LightSurfaceContainer
val OnSurfaceVariant = Color(0xFF444653)
val Outline = LightOutline

val StatusActive = AcademicSuccess
val StatusPending = AcademicWarning
val StatusSuspended = AcademicError
val StatusInfo = LightPrimaryBrand

// Admin Dashboard Colors
val AdminBackground = Color(0xFFF9FBF9)
val AdminTextPrimary = Color(0xFF1A1C1E)
val AdminTextSecondary = Color(0xFF535F70)
val AdminBorder = LightOutline
val AdminDivider = Color(0xFFF0F0F0)
val AdminSurfaceSecondary = Color(0xFFF1F4F1)

val AuditGreen = AcademicSuccess
val HeatmapBlue = LightPrimaryBrand
val HealthRed = AcademicError
