package com.example.irongym

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.irongym.adapter.ChatAdapter
import com.example.irongym.databinding.ActivityChatBinding
import com.example.irongym.entity.*
import com.example.irongym.retrofit.RetrofitGroq
import com.example.irongym.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar RecyclerView
        chatAdapter = ChatAdapter(messages)
        binding.rvChat.layoutManager = LinearLayoutManager(this)
        binding.rvChat.adapter = chatAdapter

        // Cargar mensajes guardados
        cargarMensajesGuardados()

        binding.btnClearChat.setOnClickListener {
            mostrarDialogoConfirmacion()
        }

        // Botón enviar
        binding.btnSend.setOnClickListener {
            val userInput = binding.etMessage.text.toString().trim()
            if (userInput.isNotEmpty()) {
                enviarMensaje(userInput)
                binding.etMessage.text.clear()
            }
        }

        // También al pulsar ENTER en el teclado
        binding.etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                binding.btnSend.performClick()
                true
            } else {
                false
            }
        }
    }

    private fun enviarMensaje(textoUsuario: String) {
        val horaActual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // Mostrar y guardar el mensaje del usuario
        val mensajeUsuario = Message(textoUsuario, "Tú", horaActual)
        messages.add(mensajeUsuario)
        chatAdapter.notifyItemInserted(messages.size - 1)
        binding.rvChat.scrollToPosition(messages.size - 1)
        guardarMensajeServidor(textoUsuario, "Tú")

        // Crear petición a Groq
        val request = OpenAiRequest(
            model = "llama3-70b-8192",
            messages = listOf(OpenAiRequestMessage(role = "user", content = textoUsuario))
        )

        RetrofitGroq.api.sendChatMessage(request).enqueue(object : Callback<OpenAiResponse> {
            override fun onResponse(call: Call<OpenAiResponse>, response: Response<OpenAiResponse>) {
                if (response.isSuccessful) {
                    val respuesta = response.body()?.choices?.firstOrNull()?.message?.content
                    if (!respuesta.isNullOrBlank()) {
                        val horaBot = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                        val mensajeBot = Message(respuesta.trim(), "IronBot", horaBot)
                        messages.add(mensajeBot)
                        chatAdapter.notifyItemInserted(messages.size - 1)
                        binding.rvChat.scrollToPosition(messages.size - 1)
                        guardarMensajeServidor(respuesta.trim(), "IronBot")
                    }
                } else {
                    Toast.makeText(this@ChatActivity, "Error del servidor", Toast.LENGTH_SHORT).show()
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("ChatActivity", "Error: ${response.code()} - $errorBody")
                }
            }

            override fun onFailure(call: Call<OpenAiResponse>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarMensajeServidor(texto: String, remitente: String) {
        val request = GuardarMensajeRequest(texto = texto, remitente = remitente)

        RetrofitInstance.api.guardarMensaje(request).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (!response.isSuccessful) {
                    android.util.Log.e("ChatActivity", "Error guardando mensaje en servidor: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                android.util.Log.e("ChatActivity", "Error de red guardando mensaje: ${t.message}")
            }
        })
    }

    private fun cargarMensajesGuardados() {
        RetrofitInstance.api.obtenerMensajes().enqueue(object : Callback<ObtenerMensajesResponse> {
            override fun onResponse(call: Call<ObtenerMensajesResponse>, response: Response<ObtenerMensajesResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    messages.clear() // 💬 Importante limpiar antes
                    response.body()!!.mensajes.forEach { mensajeData ->
                        val mensaje = Message(
                            text = mensajeData.texto,
                            sender = mensajeData.remitente,
                            time = mensajeData.fecha.substring(11, 16) // solo HH:mm
                        )
                        messages.add(mensaje)
                    }
                    chatAdapter.notifyDataSetChanged()
                    binding.rvChat.scrollToPosition(messages.size - 1)
                } else {
                    Toast.makeText(this@ChatActivity, "No se pudieron cargar los mensajes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ObtenerMensajesResponse>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Error de conexión cargando mensajes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun borrarConversacion() {
        RetrofitInstance.api.borrarMensajes().enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    messages.clear()
                    chatAdapter.notifyDataSetChanged()
                    Toast.makeText(this@ChatActivity, "Conversación eliminada", Toast.LENGTH_SHORT).show()

                    // 🚀 Insertar mensaje automático después de borrar
                    val mensajeSistema = Message(
                        text = "No hay mensajes. ¡Empieza una nueva conversación! 📩",
                        sender = "IronBot",
                        time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    )
                    messages.add(mensajeSistema)
                    chatAdapter.notifyItemInserted(messages.size - 1)
                    binding.rvChat.scrollToPosition(messages.size - 1)

                } else {
                    Toast.makeText(this@ChatActivity, "Error al borrar mensajes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun mostrarDialogoConfirmacion() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Confirmar")
        builder.setMessage("¿Estás seguro de que quieres borrar toda la conversación?")
        builder.setPositiveButton("Sí, borrar") { _, _ ->
            borrarConversacion() // Llama aquí tu función de borrado real
        }
        builder.setNegativeButton("Cancelar", null)
        val dialog = builder.create()
        dialog.show()
    }


}
