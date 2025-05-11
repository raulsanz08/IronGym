package com.example.irongym

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.irongym.adapter.DiaDietaAdapter
import com.example.irongym.databinding.ActivityDetalleDietaBinding
import com.example.irongym.entity.DiaDietaResponse
import com.example.irongym.retrofit.RetrofitInstance
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleDietaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetalleDietaBinding
    private var checkedStates = mutableListOf<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleDietaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dietaId = intent.getIntExtra("DIETA_ID", -1)
        val imagenUrl = intent.getStringExtra("imagenUrl")

        if (!imagenUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagenUrl)
                .into(binding.ivFondoDieta)
        }

        binding.btnBackDieta.setOnClickListener {
            finish()
        }

        binding.rvDiaDieta.layoutManager = LinearLayoutManager(this)

        // Escaneo de alimento
        binding.btnEscanear.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setPrompt("Escanea el código QR del alimento")
            integrator.setOrientationLocked(false)
            integrator.setBeepEnabled(true)
            integrator.initiateScan()
        }

        if (dietaId != -1) {
            cargarDiasDieta(dietaId)
        } else {
            Toast.makeText(this, "Dieta no válida", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarDiasDieta(dietaId: Int) {
        RetrofitInstance.api.obtenerDiasDieta(dietaId).enqueue(object : Callback<DiaDietaResponse> {
            override fun onResponse(call: Call<DiaDietaResponse>, response: Response<DiaDietaResponse>) {
                if (response.isSuccessful) {
                    val dias = response.body()?.dias ?: emptyList()
                    checkedStates = MutableList(dias.size) { false }

                    val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    for (i in dias.indices) {
                        checkedStates[i] = sharedPreferences.getBoolean("dieta_${dias[i].id}", false)
                    }

                    val adapter = DiaDietaAdapter(dias, checkedStates)
                    binding.rvDiaDieta.adapter = adapter

                    binding.btnGuardarComidas.setOnClickListener {
                        val editor = sharedPreferences.edit()
                        for (i in dias.indices) {
                            editor.putBoolean("dieta_${dias[i].id}", checkedStates[i])
                        }
                        editor.apply()
                        Toast.makeText(this@DetalleDietaActivity, "Cambios guardados", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this@DetalleDietaActivity, "Error al cargar los días", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DiaDietaResponse>, t: Throwable) {
                Toast.makeText(this@DetalleDietaActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                try {
                    val json = JSONObject(result.contents)
                    val nombre = json.getString("nombre")
                    val proteinas = json.getDouble("proteinas")
                    val carbohidratos = json.getDouble("carbohidratos")
                    val grasas = json.getDouble("grasas")

                    AlertDialog.Builder(this)
                        .setTitle("Alimento escaneado")
                        .setMessage("Nombre: $nombre\nProteínas: $proteinas g\nCarbohidratos: $carbohidratos g\nGrasas: $grasas g")
                        .setPositiveButton("OK", null)
                        .show()

                } catch (e: Exception) {
                    Toast.makeText(this, "QR inválido", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
