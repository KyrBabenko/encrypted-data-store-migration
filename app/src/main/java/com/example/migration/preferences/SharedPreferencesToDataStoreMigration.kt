package com.example.migration.preferences

import android.content.SharedPreferences
import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

class SharedPreferencesToDataStoreMigration(
    private val migrateFrom: SharedPreferences
): DataMigration<Preferences> {

    override suspend fun cleanUp() {
        // No cleanup needed
    }

    override suspend fun migrate(currentData: Preferences): Preferences {
        val preferences = currentData.toMutablePreferences()
        migrateFrom.all.forEach { (key, value) ->
            when (value) {
                is String -> preferences[stringPreferencesKey(key)] = value
                else -> throw IllegalArgumentException("Unsupported value type")
            }
        }
        return preferences
    }

    override suspend fun shouldMigrate(currentData: Preferences): Boolean {
        return currentData.toPreferences().asMap().isEmpty()
    }
}