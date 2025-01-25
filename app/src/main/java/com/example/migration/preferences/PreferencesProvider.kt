package com.example.migration.preferences

interface PreferencesProvider {
    fun getString(key: String, defaultValue: String?): String?
    fun putString(key: String, value: String)
}