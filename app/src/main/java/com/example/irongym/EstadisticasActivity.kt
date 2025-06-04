package com.example.irongym

import android.animation.ValueAnimator
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
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

    private lateinit var barraFuerza: View
    private lateinit var barraPeso: View
    private lateinit var barraLogros: View
    private lateinit var barraDisciplina: View
    private lateinit var barraAltura: View
    private lateinit var barraResistencia: View

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)


        tvRitmoCardiaco = findViewById(R.id.tvRitmoCardiaco)
        tvTituloEstadisticas = findViewById(R.id.tvTituloEstadisticas)
        ivFondoPerfil = findViewById(R.id.ivBackground)
        btnVolver = findViewById(R.id.botonVolver)

        barraFuerza = findViewById(R.id.barraFuerza)
        barraPeso = findViewById(R.id.barraPeso)
        barraLogros = findViewById(R.id.barraLogros)
        barraDisciplina = findViewById(R.id.barraDisciplina)
        barraAltura = findViewById(R.id.barraAltura)
        barraResistencia = findViewById(R.id.barraResistencia)


        Glide.with(this)
            .load("https://uk.gymshark.com/_next/image?url=https%3A%2F%2Fimages.ctfassets.net%2F8urtyqugdt2l%2F1oIrMoqckYTE96ekt5ECyT%2F51471e1e09c39541c1564bc164bd9b06%2Fdesktop-how-often-to-go-to-the-gym.jpg&w=3840&q=85")
            .into(ivFondoPerfil)


        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        token = sharedPreferences.getString("token", null) ?: ""


        btnVolver.setOnClickListener { finish() }


        cargarEstadisticas()
    }

    private fun cargarEstadisticas() {
        if (token.isEmpty()) {
            Toast.makeText(this, "Token no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitInstance.api.obtenerEstadisticas("Token $token")
            .enqueue(object : Callback<EstadisticasResponse> {
                override fun onResponse(
                    call: Call<EstadisticasResponse>,
                    response: Response<EstadisticasResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val estadisticas = response.body()!!
                        Log.d("API_Estadisticas", "Datos recibidos: $estadisticas")
                        actualizarVista(estadisticas)
                    } else {
                        Toast.makeText(
                            this@EstadisticasActivity,
                            "No se pudieron cargar las estadísticas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<EstadisticasResponse>, t: Throwable) {
                    Toast.makeText(
                        this@EstadisticasActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun actualizarVista(estadisticas: EstadisticasResponse) {
        val bpm = estadisticas.ritmoCardiaco.coerceAtLeast(80)
        tvRitmoCardiaco.text = "$bpm bpm"

        val fuerza = estadisticas.fuerza.coerceIn(0, 100)
        val peso = (estadisticas.peso / 2.0).coerceIn(0.0, 100.0).toInt()
        val logros = estadisticas.logros.coerceIn(0, 100)
        val disciplina = estadisticas.disciplina.coerceIn(0, 100)
        val altura = (estadisticas.altura / 2.5).coerceIn(0.0, 100.0).toInt()
        val resistencia = estadisticas.resistencia.coerceIn(0, 100)

        setBarraAlturaAnimada(barraFuerza, fuerza)
        setBarraAlturaAnimada(barraPeso, peso)
        setBarraAlturaAnimada(barraLogros, logros)
        setBarraAlturaAnimada(barraDisciplina, disciplina)
        setBarraAlturaAnimada(barraAltura, altura)
        setBarraAlturaAnimada(barraResistencia, resistencia)

        Log.d("DEBUG_STATS", "Fuerza: $fuerza, Peso: $peso, Altura: $altura")
    }

    private fun setBarraAlturaAnimada(barra: View, valor: Int) {
        val maxAlturaPx = 200f * resources.displayMetrics.density
        val alturaObjetivo = (valor.coerceIn(0, 100) / 100f) * maxAlturaPx

        val layoutParams = barra.layoutParams
        val alturaActual = barra.height

        val animator = ValueAnimator.ofInt(alturaActual, alturaObjetivo.toInt())
        animator.duration = 800
        animator.addUpdateListener {
            layoutParams.height = it.animatedValue as Int
            barra.layoutParams = layoutParams
        }
        animator.start()
    }

    override fun onResume() {
        super.onResume()
        cargarEstadisticas()
    }

}
