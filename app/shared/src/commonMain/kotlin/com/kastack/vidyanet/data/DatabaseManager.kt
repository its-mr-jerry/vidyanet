package com.kastack.vidyanet.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class DatabaseManager(private val settings: Settings = Settings()) {

    fun saveString(key: String, value: String) {
        settings[key] = value
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return settings[key] ?: defaultValue
    }

    fun saveInt(key: String, value: Int) {
        settings[key] = value
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return settings[key] ?: defaultValue
    }

    fun saveLong(key: String, value: Long) {
        settings[key] = value
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return settings[key] ?: defaultValue
    }

    fun saveBoolean(key: String, value: Boolean) {
        settings[key] = value
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return settings[key] ?: defaultValue
    }

    fun remove(key: String) {
        settings.remove(key)
    }

    fun clear() {
        settings.clear()
    }
}
