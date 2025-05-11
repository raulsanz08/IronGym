package com.example.irongym

import android.animation.ObjectAnimator
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.irongym.entity.EstadisticasResponse
import com.example.irongym.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EstadisticasActivity : AppCompatActivity() {

    private lateinit var tvRitmoCardiaco: TextView
    private lateinit var tvTituloEstadisticas: TextView
    private lateinit var ivFondoPerfil: ImageView
    private lateinit var btnVolver: ImageButton

    private lateinit var progressBarFuerza: ProgressBar
    private lateinit var progressBarPeso: ProgressBar
    private lateinit var progressBarLogros: ProgressBar
    private lateinit var progressBarDisciplina: ProgressBar
    private lateinit var progressBarAltura: ProgressBar
    private lateinit var progressBarResistencia: ProgressBar

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        // Inicializar vistas
        tvRitmoCardiaco = findViewById(R.id.tvRitmoCardiaco)
        tvTituloEstadisticas = findViewById(R.id.tvTituloEstadisticas)
        ivFondoPerfil = findViewById(R.id.ivBackground)
        btnVolver = findViewById(R.id.botonVolver)

        progressBarFuerza = findViewById(R.id.progressBarFuerza)
        progressBarPeso = findViewById(R.id.progressBarPeso)
        progressBarLogros = findViewById(R.id.progressBarLogros)
        progressBarDisciplina = findViewById(R.id.progressBarDisciplina)
        progressBarAltura = findViewById(R.id.progressBarAltura)
        progressBarResistencia = findViewById(R.id.progressBarResistencia)

        // Cargar imagen de fondo
        Glide.with(this)
            .load("https://uk.gymshark.com/_next/image?url=https%3A%2F%2Fimages.ctfassets.net%2F8urtyqugdt2l%2F1oIrMoqckYTE96ekt5ECyT%2F51471e1e09c39541c1564bc164bd9b06%2Fdesktop-how-often-to-go-to-the-gym.jpg&w=3840&q=85")
            .into(ivFondoPerfil)

        // Obtener token
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        token = sharedPreferences.getString("token", null) ?: ""

        // Cargar estadísticas
        cargarEstadisticas()

        // Botón volver
        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun cargarEstadisticas() {
        if (token.isEmpty()) {
            Toast.makeText(this, "Token no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitInstance.api.obtenerEstadisticas("Token $token")
            .enqueue(object : Callback<EstadisticasResponse> {
                override fun onResponse(call: Call<EstadisticasResponse>, response: Response<EstadisticasResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val estadisticas = response.body()!!
                        Log.d("API_Estadisticas", "Datos recibidos: $estadisticas")
                        actualizarVista(estadisticas)
                    } else {
                        Log.e("API_Estadisticas", "Error en la respuesta: ${response.code()}")
                        Toast.makeText(this@EstadisticasActivity, "No se pudieron cargar las estadísticas", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<EstadisticasResponse>, t: Throwable) {
                    Log.e("API_Estadisticas", "Fallo de conexión: ${t.message}")
                    Toast.makeText(this@EstadisticasActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun actualizarVista(estadisticas: EstadisticasResponse) {
        val bpm = estadisticas.ritmoCardiaco.coerceAtLeast(1)
        tvRitmoCardiaco.text = "$bpm bpm"

        // Antes de animar: asegurar que no sean indeterminados
        setProgressBarDeterminate(progressBarFuerza)
        setProgressBarDeterminate(progressBarPeso)
        setProgressBarDeterminate(progressBarLogros)
        setProgressBarDeterminate(progressBarDisciplina)
        setProgressBarDeterminate(progressBarAltura)
        setProgressBarDeterminate(progressBarResistencia)

        animateProgress(progressBarFuerza, estadisticas.fuerza)
        animateProgress(progressBarPeso, estadisticas.peso)
        animateProgress(progressBarLogros, estadisticas.logros)
        animateProgress(progressBarDisciplina, estadisticas.disciplina)
        animateProgress(progressBarAltura, estadisticas.altura.coerceAtMost(100))
        animateProgress(progressBarResistencia, estadisticas.resistencia)
    }

    private fun setProgressBarDeterminate(progressBar: ProgressBar) {
        progressBar.isIndeterminate = false
    }

    private fun animateProgress(progressBar: ProgressBar, targetProgress: Int) {
        val safeProgress = targetProgress.coerceIn(0, 100)
        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, safeProgress)
        animator.duration = 1200L
        animator.start()
    }
}
