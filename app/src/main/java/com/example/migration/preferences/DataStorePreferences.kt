package com.example.migration.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences as DataStorePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DataStorePreferences(
    context: Context,
    private val scope: CoroutineScope
) : Preferences {

    companion object {
        private const val PREFERENCES_DATA_STORE_NAME = "preferences_data_store_name"
    }

    private val Context.dataStore: DataStore<DataStorePreferences> by preferencesDataStore(name = PREFERENCES_DATA_STORE_NAME)
    private val dataStore = context.dataStore

    override fun getString(key: String, defaultValue: String?): String? = runBlocking {
        return@runBlocking try {
            val preferencesKey = stringPreferencesKey(key)
            dataStore.data.first()[preferencesKey]
        } catch (e: Exception) {
            defaultValue
        }
    }

    override fun putString(key: String, value: String) {
        scope.launch {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey(key)] = value
            }
        }
    }
}