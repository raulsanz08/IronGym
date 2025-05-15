package com.example.irongym

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.util.Locale

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        aplicarIdiomaGuardado()

        val ivBackground = findViewById<ImageView>(R.id.ivBackground)
        Glide.with(this)
            .load("https://i.blogs.es/7235b3/nathan-dumlao-nxmzxygmw8o-unsplash/450_1000.webp")
            .into(ivBackground)


        val ivProfile = findViewById<ImageView>(R.id.ivProfile)
        Glide.with(this)
            .load("https://i.blogs.es/7235b3/nathan-dumlao-nxmzxygmw8o-unsplash/450_1000.webp")
            .circleCrop()
            .into(ivProfile)


        Handler().postDelayed({
            navigateToLogin()
        }, 3000)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun aplicarIdiomaGuardado() {
        val sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val idioma = sharedPreferences.getString("LANGUAGE", "Español") ?: "Español"

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
