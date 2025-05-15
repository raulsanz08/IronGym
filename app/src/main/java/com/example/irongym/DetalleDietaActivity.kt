package com.example.irongym

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.irongym.adapter.DiaDietaAdapter
import com.example.irongym.databinding.ActivityDetalleDietaBinding
import com.example.irongym.entity.DiaDietaResponse
import com.example.irongym.retrofit.RetrofitEscaner
import com.example.irongym.retrofit.RetrofitInstance
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleDietaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetalleDietaBinding
    private var checkedStates = mutableListOf<Boolean>()

    private val scanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val intentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
        if (intentResult != null && intentResult.contents != null) {
            val rawData = intentResult.contents

            // Intentar parsear como JSON primero
            try {
                val json = JSONObject(rawData)
                val nombre = json.getString("nombre")
                val proteinas = json.getDouble("proteinas")
                val carbohidratos = json.getDouble("carbohidratos")
                val grasas = json.getDouble("grasas")

                mostrarAlerta("Alimento escaneado", "Nombre: $nombre\nProteínas: $proteinas g\nCarbohidratos: $carbohidratos g\nGrasas: $grasas g")
            } catch (e: Exception) {
                buscarAlimentoConRetrofit(rawData)
            }
        }
    }

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

        binding.btnBackDieta.setOnClickListener { finish() }

        binding.rvDiaDieta.layoutManager = LinearLayoutManager(this)

        binding.btnEscanear.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            } else {
                startQRScanner()
            }
        }

        if (dietaId != -1) {
            cargarDiasDieta(dietaId)
        } else {
            Toast.makeText(this, "Dieta no válida", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startQRScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setPrompt("Escanea el código del alimento")
        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(true)
        integrator.captureActivity = CaptureActivity::class.java
        scanLauncher.launch(integrator.createScanIntent())
    }

    private fun buscarAlimentoConRetrofit(codigo: String) {
        RetrofitEscaner.api.getProducto(codigo).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && response.body() != null) {
                    try {
                        val json = JSONObject(response.body()!!.string())
                        val status = json.optInt("status", 0)
                        if (status == 1) {
                            val producto = json.getJSONObject("product")
                            val nombre = producto.optString("product_name", "Desconocido")
                            val nutrientes = producto.optJSONObject("nutriments")
                            val proteinas = nutrientes?.optDouble("proteins", 0.0) ?: 0.0
                            val grasas = nutrientes?.optDouble("fat", 0.0) ?: 0.0
                            val carbohidratos = nutrientes?.optDouble("carbohydrates", 0.0) ?: 0.0

                            mostrarAlerta("Alimento encontrado", "Nombre: $nombre\nProteínas: $proteinas g\nGrasas: $grasas g\nCarbohidratos: $carbohidratos g")
                        } else {
                            Toast.makeText(this@DetalleDietaActivity, "Producto no encontrado", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@DetalleDietaActivity, "Error al analizar el JSON", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DetalleDietaActivity, "Error en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@DetalleDietaActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarAlerta(titulo: String, mensaje: String) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("OK", null)
                .show()
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
}
