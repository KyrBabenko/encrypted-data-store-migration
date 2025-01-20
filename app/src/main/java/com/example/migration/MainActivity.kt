package com.example.migration

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.migration.databinding.ActivityMainBinding
import com.example.migration.di.AppComponent
import com.example.migration.preferences.Preferences

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var preferences: Preferences

    companion object {
        private const val TITLE_KEY = "key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppComponent().inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.title.text = preferences.getString(TITLE_KEY, null) ?: ""
        binding.encrypt.setOnClickListener {
            val value = binding.editText.text.toString()
            preferences.putString(TITLE_KEY, value)
            binding.title.text = preferences.getString(TITLE_KEY, null) ?: ""
        }
    }
}