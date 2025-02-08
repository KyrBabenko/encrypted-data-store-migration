package com.example.migration.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.migration.MainActivity
import com.example.migration.preferences.DataStorePreferencesProvider
import com.example.migration.preferences.SharedPreferencesToDataStoreMigration

class AppComponent {

    companion object {
        private const val FILE_NAME = "secure_prefs"
    }

    private lateinit var shredPreferences: SharedPreferences

    fun inject(mainActivity: MainActivity) {
        initSharedPreferences(mainActivity)
        val preferencesProvider = DataStorePreferencesProvider(
            mainActivity,
            provideMigrations()
        )
        mainActivity.preferences = preferencesProvider
    }

    private fun initSharedPreferences(context: Context) {
        val sharedPreferences by lazy {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
        shredPreferences = sharedPreferences
    }

    private fun provideMigrations(): List<DataMigration<Preferences>> {
        return listOf(provideMigration())
    }
    
    private fun provideMigration(): DataMigration<Preferences> {
        return SharedPreferencesToDataStoreMigration(shredPreferences)
    }
}