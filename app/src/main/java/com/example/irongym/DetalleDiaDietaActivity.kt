package com.example.irongym

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.irongym.adapter.ComidaDiaAdapter
import com.example.irongym.entity.ComidasResponse
import com.example.irongym.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleDiaDietaActivity : AppCompatActivity() {

    private lateinit var rvComidas: RecyclerView
    private lateinit var tvTitulo: TextView
    private lateinit var ivDia: ImageView
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_dieta_dia)

        rvComidas = findViewById(R.id.rvComidas)
        tvTitulo = findViewById(R.id.tvTituloDiaDieta)
        ivDia = findViewById(R.id.ivDiaDieta)
        btnBack = findViewById(R.id.btnBackDiaDieta)

        val diaId = intent.getIntExtra("DIA_DIETA_ID", -1)
        val titulo = intent.getStringExtra("DIA_DIETA_NOMBRE") ?: "Día de dieta"
        val imagenUrl = intent.getStringExtra("IMAGEN_URL") ?: ""


        Log.d("DetalleDiaDieta", "ID recibido: $diaId")

        tvTitulo.text = titulo

        if (imagenUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imagenUrl)
                .into(ivDia)
        }

        btnBack.setOnClickListener {
            finish()
        }

        rvComidas.layoutManager = LinearLayoutManager(this)

        if (diaId != -1) {
            cargarComidas(diaId)
        } else {
            Toast.makeText(this, "Error al cargar el día de dieta", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun cargarComidas(diaId: Int) {
        RetrofitInstance.api.obtenerComidasDelDia(diaId)
            .enqueue(object : Callback<ComidasResponse> {
                override fun onResponse(call: Call<ComidasResponse>, response: Response<ComidasResponse>) {
                    Log.d("DetalleDiaDieta", "Respuesta recibida: ${response.body()}")
                    if (response.isSuccessful && response.body()?.success == true) {
                        val comidas = response.body()?.comidas ?: emptyList()
                        Log.d("DetalleDiaDieta", "Comidas recibidas: ${comidas.size}")
                        rvComidas.adapter = ComidaDiaAdapter(comidas)
                    } else {
                        Log.e("DetalleDiaDieta", "Respuesta fallida: ${response.code()} - ${response.message()}")
                        Toast.makeText(this@DetalleDiaDietaActivity, "Error al obtener comidas", Toast.LENGTH_SHORT).show()
                    }
                }


                override fun onFailure(call: Call<ComidasResponse>, t: Throwable) {
                    Log.e("DetalleDiaDieta", "Fallo de conexión: ${t.message}")
                    Toast.makeText(this@DetalleDiaDietaActivity, "Fallo de conexión", Toast.LENGTH_SHORT).show()
                }

            })
    }
}
