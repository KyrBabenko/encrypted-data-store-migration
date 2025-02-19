package com.example.migration.preferences

import android.content.SharedPreferences

class SharedPreferencesProvider(
    private val sharedPreferences: SharedPreferences
): PreferencesProvider {

    override fun getString(key: String, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    override fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getAll(): Map<String, String> {
        return sharedPreferences.all.mapNotNull { (key, value) ->
            if (value is String) key to value else null
        }.toMap()
    }
}