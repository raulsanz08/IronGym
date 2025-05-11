package com.example.irongym

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.irongym.adapter.DietaAdapter
import com.example.irongym.adapter.RutinaAdapter
import com.example.irongym.databinding.ActivityMainBinding
import com.example.irongym.entity.DietaResponse
import com.example.irongym.entity.RutinaResponse
import com.example.irongym.retrofit.RetrofitInstance
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var rutinaAdapter: RutinaAdapter
    private lateinit var dietaAdapter: DietaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        RetrofitInstance.initialize(this)

        // Configurar RecyclerViews
        binding.rvRutinas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvDietas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Asignar adapters vacíos inicialmente para evitar el warning
        rutinaAdapter = RutinaAdapter(emptyList())
        dietaAdapter = DietaAdapter(emptyList())
        binding.rvRutinas.adapter = rutinaAdapter
        binding.rvDietas.adapter = dietaAdapter

        // Cargar datos desde la API
        obtenerRutinas()
        obtenerDietas()

        // Botón logout
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            logout()
        }

        binding.fabChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        binding.etSearch.addTextChangedListener { editable ->
            val query = editable?.toString() ?: ""

            rutinaAdapter.filter(query)
            dietaAdapter.filter(query)

            // 🔥 Ahora comprobamos si hay o no resultados
            val hayRutinas = rutinaAdapter.itemCount > 0
            val hayDietas = dietaAdapter.itemCount > 0

            if (!hayRutinas && !hayDietas) {
                binding.tvNoResults.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate().alpha(1f).setDuration(300).start()
                }
                binding.rvRutinas.visibility = View.GONE
                binding.rvDietas.visibility = View.GONE
                binding.tvPlanEntrenamiento.visibility = View.GONE
                binding.tvAsistenteNutricional.visibility = View.GONE
            } else {
                if (binding.tvNoResults.visibility == View.VISIBLE) {
                    binding.tvNoResults.animate().alpha(0f).setDuration(300).withEndAction {
                        binding.tvNoResults.visibility = View.GONE
                    }.start()
                }
                binding.rvRutinas.visibility = View.VISIBLE
                binding.rvDietas.visibility = View.VISIBLE
                binding.tvPlanEntrenamiento.visibility = View.VISIBLE
                binding.tvAsistenteNutricional.visibility = View.VISIBLE
            }

        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    false
                }
                R.id.nav_progreso -> {
                    startActivity(Intent(this, EstadisticasActivity::class.java))
                    false
                }
                R.id.nav_configuracion -> {
                    startActivity(Intent(this, ConfiguracionActivity::class.java))
                    false
                }
                R.id.nav_notificaciones -> {
                    startActivity(Intent(this, NotificacionesActivity::class.java))
                    false
                }
                R.id.nav_modo_desafio -> {
                    startActivity(Intent(this, ModoDesafioActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }

    private fun obtenerRutinas() {
        RetrofitInstance.api.obtenerRutinas().enqueue(object : Callback<RutinaResponse> {
            override fun onResponse(call: Call<RutinaResponse>, response: Response<RutinaResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val rutinas = response.body()!!.rutinas
                    Log.d("MainActivity", "Rutinas recibidas: ${rutinas.size}")

                    // Actualizar el adapter
                    rutinaAdapter.updateData(rutinas)
                } else {
                    Toast.makeText(this@MainActivity, "Error cargando rutinas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RutinaResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun obtenerDietas() {
        RetrofitInstance.api.obtenerDietas().enqueue(object : Callback<DietaResponse> {
            override fun onResponse(call: Call<DietaResponse>, response: Response<DietaResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val dietas = response.body()!!.dietas
                    Log.d("MainActivity", "Dietas recibidas: ${dietas.size}")

                    // Actualizar el adapter
                    dietaAdapter.updateData(dietas)
                } else {
                    Toast.makeText(this@MainActivity, "Error cargando dietas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DietaResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("token").apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
