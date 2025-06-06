package com.example.irongym

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.irongym.adapter.NotificacionAdapter
import com.example.irongym.api.ApiService
import com.example.irongym.entity.NotificacionUpdateResponse
import com.example.irongym.entity.NotificacionesResponse
import com.example.irongym.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificacionesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificacionAdapter: NotificacionAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String
    private val apiService: ApiService = RetrofitInstance.api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificaciones)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        token = sharedPreferences.getString("token", null) ?: ""

        recyclerView = findViewById(R.id.rvNotificaciones)
        recyclerView.layoutManager = LinearLayoutManager(this)
        notificacionAdapter = NotificacionAdapter(mutableListOf())
        recyclerView.adapter = notificacionAdapter

        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        btnGuardar.setOnClickListener {
            val notificacionesActualizadas = notificacionAdapter.getNotificaciones()

            for (notificacion in notificacionesActualizadas) {
                val body = NotificacionUpdateResponse(notificacion.activo)

                apiService.actualizarNotificacion(notificacion.id, body).enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                        if (response.isSuccessful) {
                            Log.d("GUARDAR", "Notificación ${notificacion.id} actualizada con éxito")
                        } else {
                            Log.e("GUARDAR", "Error actualizando notificación ${notificacion.id}")
                        }
                    }

                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        Log.e("GUARDAR", "Error de conexión actualizando notificación ${notificacion.id}: ${t.message}")
                    }
                })
            }

            Toast.makeText(this, "Guardando cambios...", Toast.LENGTH_SHORT).show()
        }




        val btnBack = findViewById<ImageButton>(R.id.btnReturn)
        btnBack.setOnClickListener {
            finish()
        }

        obtenerNotificaciones()
    }

    private fun obtenerNotificaciones() {
        apiService.obtenerNotificaciones("Token $token").enqueue(object : Callback<NotificacionesResponse> {
            override fun onResponse(
                call: Call<NotificacionesResponse>,
                response: Response<NotificacionesResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val notificacionesResponse = response.body()!!
                    if (notificacionesResponse.success) {
                        val notificaciones = notificacionesResponse.notificaciones
                        Log.d("API", "Notificaciones recibidas: ${notificaciones.size}")
                        notificacionAdapter.updateNotificaciones(notificaciones)
                    } else {
                        Toast.makeText(
                            this@NotificacionesActivity,
                            "Error al obtener notificaciones",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@NotificacionesActivity,
                        "Respuesta no válida del servidor",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<NotificacionesResponse>, t: Throwable) {
                Toast.makeText(
                    this@NotificacionesActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }
}