package com.example.irongym

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.irongym.adapter.EjercicioDiaAdapter
import com.example.irongym.entity.EjerciciosResponse
import com.example.irongym.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleEjercicioActivity : AppCompatActivity() {

    private lateinit var rvEjercicios: RecyclerView
    private lateinit var tvTitulo: TextView
    private lateinit var ivDia: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_ejercicio)

        rvEjercicios = findViewById(R.id.rvEjercicios)
        tvTitulo = findViewById(R.id.tvTituloDia)
        ivDia = findViewById(R.id.ivDia)

        val diaId = intent.getIntExtra("DIA_ID", -1)
        val titulo = intent.getStringExtra("DIA_NOMBRE") ?: "Día de entrenamiento"
        val imagenUrl = intent.getStringExtra("IMAGEN_URL") ?: ""

        val btnBack = findViewById<ImageButton>(R.id.btnBackEjercicio)

        btnBack.setOnClickListener {
            finish()
        }

        tvTitulo.text = titulo

        if (imagenUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imagenUrl)
                .into(ivDia)
        }

        rvEjercicios.layoutManager = LinearLayoutManager(this)

        if (diaId != -1) {
            cargarEjercicios(diaId)
        } else {
            Toast.makeText(this, "Error al cargar día", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun cargarEjercicios(diaId: Int) {
        RetrofitInstance.api.obtenerEjerciciosDelDia(diaId)
            .enqueue(object : Callback<EjerciciosResponse> {
                override fun onResponse(
                    call: Call<EjerciciosResponse>,
                    response: Response<EjerciciosResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val ejercicios = response.body()?.ejercicios ?: emptyList()
                        rvEjercicios.adapter = EjercicioDiaAdapter(ejercicios)
                    } else {
                        Toast.makeText(this@DetalleEjercicioActivity, "Error al obtener ejercicios", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<EjerciciosResponse>, t: Throwable) {
                    Toast.makeText(this@DetalleEjercicioActivity, "Fallo de conexión", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
