package com.example.migration

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.migration.databinding.ActivityMainBinding
import com.example.migration.di.AppComponent
import com.example.migration.preferences.PreferencesProvider

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var preferences: PreferencesProvider

    companion object {
        private const val TITLE_KEY = "key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppComponent().inject(this)
        super.onCreate(savedInstanceState)
        logAllKeys()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.title.text = preferences.getString(TITLE_KEY, null) ?: ""
        binding.encrypt.setOnClickListener {
            val value = binding.editText.text.toString()
            preferences.putString(TITLE_KEY, value)
            binding.title.text = preferences.getString(TITLE_KEY, null) ?: ""
            logAllKeys()
        }
    }

    private fun logAllKeys() {
        Log.d("TAG11", "all keys: ${preferences.getAll()}")
    }
}