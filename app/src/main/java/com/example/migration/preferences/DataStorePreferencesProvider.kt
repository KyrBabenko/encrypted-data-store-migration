package com.example.migration.preferences

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.migration.encryption.EncryptedDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DataStorePreferencesProvider(
    context: Context,
    private val migrations: List<DataMigration<Preferences>>
) : PreferencesProvider {

    companion object {
        private const val PREFERENCES_DATA_STORE_NAME = "preferences_data_store_name"
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = PREFERENCES_DATA_STORE_NAME,
        produceMigrations = { migrations }
    )
    private val dataStore = context.dataStore

    private val encryption = EncryptedDataStore(context)

    override fun getString(key: String, defaultValue: String?): String? = runBlocking {
        return@runBlocking try {
            val encryptedKey = encryption.encryptKey(key)
            val preferencesKey = stringPreferencesKey(encryptedKey)
            val encryptedValue = dataStore.data.first()[preferencesKey]

            encryptedValue?.let {
                encryption.decryptValue(it)
            } ?: defaultValue
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override fun putString(key: String, value: String): Unit = runBlocking {
        try {
            val encryptedKey = encryption.encryptKey(key)
            val preferencesKey = stringPreferencesKey(encryptedKey)
            val encryptedValue = encryption.encryptValue(value)

            dataStore.edit { preferences ->
                preferences[preferencesKey] = encryptedValue
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
