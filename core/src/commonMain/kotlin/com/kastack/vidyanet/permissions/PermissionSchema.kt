package com.kastack.vidyanet.permissions

import com.kastack.vidyanet.models.role.PermissionAction

object PermissionSchema {
    
    object Dashboard {
        const val MODULE = "DASHBOARD"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Admissions {
        const val MODULE = "ADMISSIONS"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Students {
        const val MODULE = "STUDENTS"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Parents {
        const val MODULE = "PARENTS"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Staff {
        const val MODULE = "STAFF"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Academics {
        const val MODULE = "ACADEMICS"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Attendance {
        const val MODULE = "ATTENDANCE"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Timetable {
        const val MODULE = "TIMETABLE"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Examinations {
        const val MODULE = "EXAMINATIONS"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Finance {
        const val MODULE = "FINANCE"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Library {
        const val MODULE = "LIBRARY"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Transport {
        const val MODULE = "TRANSPORT"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Inventory {
        const val MODULE = "INVENTORY"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Hostel {
        const val MODULE = "HOSTEL"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Sports {
        const val MODULE = "SPORTS"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Communication {
        const val MODULE = "COMMUNICATION"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Reports {
        const val MODULE = "REPORTS"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    object Settings {
        const val MODULE = "SETTINGS"
        val VIEW = format(MODULE, PermissionAction.VIEW)
        val CREATE = format(MODULE, PermissionAction.CREATE)
        val EDIT = format(MODULE, PermissionAction.EDIT)
        val DELETE = format(MODULE, PermissionAction.DELETE)
        val EXPORT = format(MODULE, PermissionAction.EXPORT)
    }

    // Helper to format permission strings
    fun format(module: String, action: PermissionAction): String {
        return "${module.uppercase()}_${action.name}"
    }
}
