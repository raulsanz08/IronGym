package com.example.irongym

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.irongym.entity.PerfilUsuario
import com.example.irongym.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilActivity : AppCompatActivity() {

    private lateinit var tvNombreUsuario: TextView
    private lateinit var tvPesoUsuario: TextView
    private lateinit var tvAlturaUsuario: TextView
    private lateinit var etNombre: EditText
    private lateinit var etPeso: EditText
    private lateinit var etAltura: EditText
    private lateinit var btnActualizar: Button
    private lateinit var ivAvatar: ImageView
    private lateinit var ivFondoPerfil: ImageView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)

        // Inicializar vistas
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario)
        tvPesoUsuario = findViewById(R.id.tvPesoUsuario)
        tvAlturaUsuario = findViewById(R.id.tvAlturaUsuario)
        etNombre = findViewById(R.id.etNombre)
        etPeso = findViewById(R.id.etPeso)
        etAltura = findViewById(R.id.etAltura)
        btnActualizar = findViewById(R.id.btnActualizar)
        ivAvatar = findViewById(R.id.ivAvatar)
        ivFondoPerfil = findViewById(R.id.ivFondoPerfil)

        // Obtener el token
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        token = sharedPreferences.getString("TOKEN", null) ?: ""

        cargarDatosPerfil()

        btnActualizar.setOnClickListener { guardarDatosPerfil() }

        // Cargar imagen de fondo
        Glide.with(this)
            .load("https://hips.hearstapps.com/hmg-prod/images/mamdouh-big-ramy-elssbiay-competes-in-the-arnold-classic-as-news-photo-1628075584.jpg?crop=1xw:0.66676xh;center,top&resize=640:*")
            .placeholder(R.drawable.ic_launcher_background)
            .into(ivFondoPerfil)

        // Cargar imagen de avatar
        Glide.with(this)
            .load("https://hips.hearstapps.com/hmg-prod/images/captura-de-pantalla-2024-06-10-a-las-10-12-46-6666b588c5fd4.jpg?crop=1.00xw:0.620xh;0,0.00803xh&resize=1200:*")
            .circleCrop()
            .into(ivAvatar)

        val btnBack = findViewById<ImageButton>(R.id.btnBackPerfil)
        btnBack.setOnClickListener { finish() }
    }

    private fun cargarDatosPerfil() {
        if (token.isEmpty()) {
            Toast.makeText(this, "Token no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitInstance.api.obtenerPerfil("Token $token")
            .enqueue(object : Callback<PerfilUsuario> {
                override fun onResponse(call: Call<PerfilUsuario>, response: Response<PerfilUsuario>) {
                    if (response.isSuccessful && response.body() != null) {
                        val perfil = response.body()!!

                        tvNombreUsuario.text = perfil.nombre
                        tvPesoUsuario.text = "Peso: ${perfil.peso} kg"
                        tvAlturaUsuario.text = "Altura: ${perfil.altura} cm"

                        etNombre.setText(perfil.nombre)
                        etPeso.setText(perfil.peso.toString())
                        etAltura.setText(perfil.altura.toString())
                    } else {
                        Toast.makeText(this@PerfilActivity, "No se pudieron cargar los datos", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PerfilUsuario>, t: Throwable) {
                    Toast.makeText(this@PerfilActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun guardarDatosPerfil() {
        if (token.isEmpty()) {
            Toast.makeText(this, "Token no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val nombre = etNombre.text.toString()
        val peso = etPeso.text.toString().toFloatOrNull() ?: 0f
        val altura = etAltura.text.toString().toIntOrNull() ?: 0

        val body = HashMap<String, Any>()
        body["nombre"] = nombre
        body["peso"] = peso
        body["altura"] = altura

        RetrofitInstance.api.guardarPerfil("Token $token", body)
            .enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@PerfilActivity, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()

                        // 🔥 ACTUALIZAMOS LAS VISTAS
                        tvNombreUsuario.text = nombre
                        tvPesoUsuario.text = "Peso: $peso kg"
                        tvAlturaUsuario.text = "Altura: $altura cm"
                    } else {
                        Toast.makeText(this@PerfilActivity, "Error al guardar perfil", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    Toast.makeText(this@PerfilActivity, "Fallo de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
