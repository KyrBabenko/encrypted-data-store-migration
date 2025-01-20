package com.example.migration.preferences

interface Preferences {

    fun getString(key: String, defaultValue: String?): String?

    fun putString(key: String, value: String)
}