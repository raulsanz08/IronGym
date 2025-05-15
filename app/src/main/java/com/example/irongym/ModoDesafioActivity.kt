package com.example.irongym

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.irongym.adapter.DesafioAdapter
import com.example.irongym.entity.DesafioResponse
import com.example.irongym.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModoDesafioActivity : AppCompatActivity() {

    private lateinit var desafioAdapter: DesafioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modo_desafio)


        val recyclerView: RecyclerView = findViewById(R.id.rvDesafios)
        recyclerView.layoutManager = LinearLayoutManager(this)


        desafioAdapter = DesafioAdapter { desafio, isChecked ->
            desafio.activo = isChecked
        }
        recyclerView.adapter = desafioAdapter


        val ivFondoPerfil: ImageView = findViewById(R.id.ivFondoDesafio)
        Glide.with(this@ModoDesafioActivity)
            .load("https://resizer.glanacion.com/resizer/v2/desafios-fitness-estas-propuestas-se-imponen-en-ITEGZ653G5HJXAJEXYEXROV62Q.JPG?auth=d6ce9c0d05b1b1458298eff16164eeea76a4402a64683578d992a14bc98fa7a0&width=1200&quality=70&smart=false&height=710")
            .into(ivFondoPerfil)


        obtenerDesafios()


        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }


        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        btnGuardar.setOnClickListener {
            actualizarDesafios()
        }
    }

    private fun obtenerDesafios() {
        RetrofitInstance.api.obtenerDesafios().enqueue(object : Callback<DesafioResponse> {
            override fun onResponse(call: Call<DesafioResponse>, response: Response<DesafioResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val desafioResponse = response.body()
                    if (desafioResponse != null && desafioResponse.success) {
                        val desafios = desafioResponse.desafios
                        desafioAdapter.submitList(desafios)
                    } else {
                        Toast.makeText(this@ModoDesafioActivity, "No hay desafíos disponibles", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ModoDesafioActivity, "Error al cargar los desafíos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DesafioResponse>, t: Throwable) {
                Toast.makeText(this@ModoDesafioActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun actualizarDesafios() {
        val desafiosActualizados = desafioAdapter.currentList.filter { it.cambiado }

        if (desafiosActualizados.isEmpty()) {
            Toast.makeText(this, "No hay cambios para guardar", Toast.LENGTH_SHORT).show()
            return
        }

        for (desafio in desafiosActualizados) {
            val body = mapOf("completado" to desafio.activo)

            RetrofitInstance.api.actualizarDesafio(desafio.id, body)
                .enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                        if (response.isSuccessful) {
                            Log.d("REALIZAR", "Desafío ${desafio.id} actualizado correctamente")
                            desafio.cambiado = false
                        } else {
                            Log.e("REALIZAR", "Error actualizando desafío ${desafio.id}")
                        }
                    }

                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        Log.e("REALIZAR", "Error de red actualizando desafío ${desafio.id}: ${t.message}")
                    }
                })
        }

        Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
    }


}
