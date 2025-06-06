package com.example.irongym.api

import com.example.irongym.entity.AuthResponse
import com.example.irongym.entity.ComidasResponse
import com.example.irongym.entity.ConfiguracionRequest
import com.example.irongym.entity.DesafioResponse
import com.example.irongym.entity.DiaDietaResponse
import com.example.irongym.entity.DiasResponse
import com.example.irongym.entity.DietaResponse
import com.example.irongym.entity.EjerciciosResponse
import com.example.irongym.entity.EstadisticasResponse
import com.example.irongym.entity.GuardarMensajeRequest
import com.example.irongym.entity.NotificacionUpdateResponse
import com.example.irongym.entity.NotificacionesResponse
import com.example.irongym.entity.ObtenerMensajesResponse
import com.example.irongym.entity.PerfilUsuario
import com.example.irongym.entity.RutinaResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("/api/login/")
    fun loginUser(@Body requestBody: HashMap<String, String>): Call<AuthResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/register/")
    fun registerUser(@Body requestBody: HashMap<String, String>): Call<AuthResponse>

    @GET("/api/rutinas/")
    fun obtenerRutinas(): Call<RutinaResponse>

    @GET("/api/dietas/")
    fun obtenerDietas(): Call<DietaResponse>

    @GET("/api/rutinas/{rutina_id}/dias/")
    fun obtenerDiasEntrenamiento(@Path("rutina_id") rutinaId: Int): Call<DiasResponse>

    @GET("/api/dietas/{id}/dias/")
    fun obtenerDiasDieta(@Path("id") dietaId: Int): Call<DiaDietaResponse>

    @GET("/api/ejercicios-dia/{dia_id}/")
    fun obtenerEjerciciosDelDia(@Path("dia_id") diaId: Int): Call<EjerciciosResponse>

    @GET("/api/comidas-dia/{id}/")
    fun obtenerComidasDelDia(@Path("id") diaId: Int): Call<ComidasResponse>

    @GET("/api/perfil/")
    fun obtenerPerfil(@Header("Authorization") token: String): Call<PerfilUsuario>

    @POST("/api/perfil/update/")
    fun guardarPerfil(@Header("Authorization") token: String, @Body body: HashMap<String, Any>): Call<Map<String, Any>>

    @GET("/api/estadisticas/")
    fun obtenerEstadisticas(@Header("Authorization") token: String): Call<EstadisticasResponse>

    @GET("/api/notificaciones/")
    fun obtenerNotificaciones(@Header("Authorization") token: String): Call<NotificacionesResponse>

    @PUT("/api/notificaciones/{id}/update/")
    fun actualizarNotificacion(@Path("id") id: Int, @Body body: NotificacionUpdateResponse): Call<Map<String, Any>>

    @GET("/api/desafios/")
    fun obtenerDesafios(@Header("Authorization") token: String): Call<DesafioResponse>

    @PUT("/api/desafios/{id}/")
    fun actualizarDesafio(@Path("id") id: Int, @Header("Authorization") token: String, @Body body: Map<String, Any>): Call<Map<String, Any>>

    @POST("/api/configuracion/")
    fun actualizarConfiguracion(@Header("Authorization") token: String, @Body configuracion: ConfiguracionRequest): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("api/mensajes/guardar/")
    fun guardarMensaje(@Body request: GuardarMensajeRequest): Call<Map<String, Any>>

    @Headers("Content-Type: application/json")
    @GET("api/mensajes/obtener/")
    fun obtenerMensajes(): Call<ObtenerMensajesResponse>

    @DELETE("/api/mensajes/borrar/")
    fun borrarMensajes(): Call<Map<String, Any>>

    @GET("api/v1/product/{code}.json")
    fun getProducto(@Path("code") codigo: String): Call<ResponseBody>


}
