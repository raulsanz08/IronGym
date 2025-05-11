package com.example.irongym

import android.app.Application
import android.content.SharedPreferences
import com.example.irongym.retrofit.RetrofitInstance
import java.util.Locale

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitInstance.initialize(this)
        aplicarIdiomaGuardado()
    }
    private fun aplicarIdiomaGuardado() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val idioma = sharedPreferences.getString("LANGUAGE", "Español")

        val locale = when (idioma) {
            "Español" -> Locale("es", "ES")
            "Inglés" -> Locale("en", "US")
            else -> Locale("es", "ES")
        }

        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
