package com.example.irongym

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.irongym.adapter.DiaEntrenamientoAdapter
import com.example.irongym.entity.DiasResponse
import com.example.irongym.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleRutinaActivity : AppCompatActivity() {
    private lateinit var rvDias: RecyclerView
    private lateinit var adapter: DiaEntrenamientoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_rutina)

        rvDias = findViewById(R.id.rvDiasEntrenamiento)
        rvDias.layoutManager = LinearLayoutManager(this)

        val rutinaId = intent.getIntExtra("RUTINA_ID", -1)
        val imagenUrl = intent.getStringExtra("imagenUrl")
        val fondoImageView = findViewById<ImageView>(R.id.ivFondoRutina)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        btnBack.setOnClickListener {
            finish()
        }

        if (!imagenUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagenUrl)
                .into(fondoImageView)
        }

        if (rutinaId != -1) {
            cargarDias(rutinaId)
        } else {
            Toast.makeText(this, "Error: rutina no encontrada", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnGuardar.setOnClickListener {
            guardarEstadosCheckBox()
        }
    }

    private fun cargarDias(rutinaId: Int) {
        RetrofitInstance.api.obtenerDiasEntrenamiento(rutinaId).enqueue(object : Callback<DiasResponse> {
            override fun onResponse(call: Call<DiasResponse>, response: Response<DiasResponse>) {
                if (response.isSuccessful) {
                    val dias = response.body()?.dias ?: emptyList()
                    adapter = DiaEntrenamientoAdapter(dias)
                    rvDias.adapter = adapter

                    val sharedPreferences = getSharedPreferences("DiasPrefs", MODE_PRIVATE)
                    val estadosGuardados = mutableMapOf<Int, Boolean>()
                    dias.forEach {
                        val estado = sharedPreferences.getBoolean("dia_${it.id}", false)
                        estadosGuardados[it.id] = estado
                    }
                    adapter.cargarEstadosGuardados(estadosGuardados)

                } else {
                    Toast.makeText(this@DetalleRutinaActivity, "Error al obtener los días", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DiasResponse>, t: Throwable) {
                Toast.makeText(this@DetalleRutinaActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarEstadosCheckBox() {
        val estados = adapter.obtenerEstadosCheckBox()
        val sharedPreferences = getSharedPreferences("DiasPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        for ((id, isChecked) in estados) {
            editor.putBoolean("dia_$id", isChecked)
        }

        editor.apply()
        Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
    }
}

