package com.example.migration.preferences

import android.content.SharedPreferences
import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.migration.encryption.EncryptedDataStore

class SharedPreferencesToDataStoreMigration(
    private val migrateFrom: SharedPreferences
) : DataMigration<Preferences> {

    private val encryption = EncryptedDataStore()

    override suspend fun cleanUp() {
        // No cleanup needed
    }

    override suspend fun migrate(currentData: Preferences): Preferences {
        val preferences = currentData.toMutablePreferences()
        migrateFrom.all.forEach { (key, value) ->
            when (value) {
                is String -> {
                    val encryptedKey = encryption.encryptKey(key).toString(Charsets.UTF_8)
                    val encryptedValue = encryption.encryptValue(value).toString(Charsets.UTF_8)
                    preferences[stringPreferencesKey(encryptedKey)] = encryptedValue
                }
                else -> throw IllegalArgumentException("Unsupported value type")
            }
        }
        return preferences
    }

    override suspend fun shouldMigrate(currentData: Preferences): Boolean {
        return currentData.toPreferences().asMap().isEmpty()
    }
}