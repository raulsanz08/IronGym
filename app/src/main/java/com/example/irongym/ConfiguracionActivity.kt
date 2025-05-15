package com.example.irongym

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.irongym.retrofit.RetrofitInstance
import com.example.irongym.entity.ConfiguracionRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ConfiguracionActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etContrasena: EditText
    private lateinit var checkNotificaciones: CheckBox
    private lateinit var spinnerIdioma: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnCerrarSesion: Button
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        aplicarIdiomaGuardado()

        setContentView(R.layout.activity_configuracion)


        etEmail = findViewById(R.id.etNombreUsuario)
        etContrasena = findViewById(R.id.etContrasena)
        checkNotificaciones = findViewById(R.id.checkboxNotificaciones)
        spinnerIdioma = findViewById(R.id.spinnerIdioma)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        btnBack = findViewById(R.id.btnBack)


        val idiomas = arrayOf("Español", "Inglés")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, idiomas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerIdioma.adapter = adapter


        btnBack.setOnClickListener {
            finish()
        }


        btnGuardar.setOnClickListener {
            val email = etEmail.text.toString()
            val contrasena = etContrasena.text.toString()
            val notificaciones = checkNotificaciones.isChecked
            val idioma = spinnerIdioma.selectedItem.toString()

            val sharedPreferences: SharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
            val idiomaGuardado = sharedPreferences.getString("LANGUAGE", "Español") ?: "Español"


            if (idioma != idiomaGuardado) {
                cambiarIdioma(idioma)
            }


            val configuracion = ConfiguracionRequest(
                email = if (email.isNotEmpty()) email else "",
                contrasena = if (contrasena.isNotEmpty()) contrasena else "",
                notificaciones = notificaciones,
                idioma = idioma
            )

            actualizarConfiguracion(configuracion)
        }


        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cambiarIdioma(idioma: String) {
        val locale = when (idioma) {
            "Español" -> Locale("es", "ES")
            "Inglés" -> Locale("en", "US")
            else -> Locale("es", "ES")
        }

        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)


        val sharedPreferences: SharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("LANGUAGE", idioma)
        editor.apply()


        Toast.makeText(
            this,
            if (idioma == "Español") "Idioma cambiado a Español" else "Language changed to English",
            Toast.LENGTH_SHORT
        ).show()


        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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

    private fun actualizarConfiguracion(configuracion: ConfiguracionRequest) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token == null) {
            Toast.makeText(this, "Error: no se encontró el token", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitInstance.api.actualizarConfiguracion("Token $token", configuracion)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@ConfiguracionActivity,
                            "Cambios guardados exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ConfiguracionActivity,
                            "Error al guardar los cambios",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        this@ConfiguracionActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun cerrarSesion() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()


        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
