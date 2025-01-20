package com.example.migration.di

import com.example.migration.MainActivity
import com.example.migration.preferences.SharedPreferences

class AppComponent {

    fun inject(mainActivity: MainActivity) {
        mainActivity.preferences = SharedPreferences(mainActivity)
    }
}