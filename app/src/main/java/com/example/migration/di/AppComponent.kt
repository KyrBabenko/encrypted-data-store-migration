package com.example.migration.di

import com.example.migration.MainActivity
import com.example.migration.preferences.SharedPreferencesProvider

class AppComponent {

    fun inject(mainActivity: MainActivity) {
        mainActivity.preferences = SharedPreferencesProvider(mainActivity)
    }
}