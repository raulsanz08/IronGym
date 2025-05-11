package com.example.irongym.entity

data class ObtenerMensajesResponse(
    val success: Boolean,
    val mensajes: List<MensajeResponse>
)
