package com.example.irongym.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irongym.entity.AuthResponse
import com.example.irongym.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel : ViewModel() {
    private val _authResult = MutableLiveData<AuthResponse>()
    val authResult: LiveData<AuthResponse> get() = _authResult

    fun registerUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val requestBody = hashMapOf("email" to email, "password" to password)
                val response = RetrofitInstance.api.registerUser(requestBody).execute()

                if (response.isSuccessful) {
                    _authResult.postValue(response.body() ?: AuthResponse(false, "Error en el servidor"))
                } else {
                    _authResult.postValue(AuthResponse(false, "Registro fallido"))
                }

            } catch (e: Exception) {
                _authResult.postValue(AuthResponse(false, "No se pudo conectar al servidor"))
            }
        }
    }

    fun loginUser(email: String, password: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val requestBody = hashMapOf("email" to email, "password" to password)

                val response = RetrofitInstance.api.loginUser(requestBody).execute()

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("AuthViewModel", "Respuesta API: $body")

                    if (body != null && body.success) {
                        Log.d("AuthViewModel", "Inicio de sesión exitoso con token: ${body.token}")

                        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("token", body.token)
                        editor.putInt("usuario_id", body.usuario_id ?: -1)
                        editor.apply()

                        _authResult.postValue(
                            AuthResponse(
                                success = true,
                                message = "Inicio de sesión exitoso",
                                token = body.token,
                                usuario_id = body.usuario_id
                            )
                        )
                    } else {
                        Log.e("AuthViewModel", "Error en el login: ${body?.message}")
                        _authResult.postValue(AuthResponse(false, "Credenciales incorrectas"))
                    }
                } else {
                    Log.e("AuthViewModel", "Error HTTP ${response.code()} - ${response.message()}")
                    _authResult.postValue(AuthResponse(false, "Error en el servidor"))
                }

            } catch (e: HttpException) {
                Log.e("AuthViewModel", "Error HTTP: ${e.message}")
                _authResult.postValue(AuthResponse(false, "Error en el servidor"))
            } catch (e: IOException) {
                Log.e("AuthViewModel", "Error de conexión: ${e.message}")
                _authResult.postValue(AuthResponse(false, "No se pudo conectar al servidor"))
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error desconocido: ${e.message}")
                _authResult.postValue(AuthResponse(false, "Error desconocido"))
            }
        }
    }

}
