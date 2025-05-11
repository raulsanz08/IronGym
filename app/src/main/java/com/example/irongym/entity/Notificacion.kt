package com.example.irongym.entity

import java.io.Serializable

data class Notificacion(
    val id: Int,
    val descripcion: String,
    var activo: Boolean,
    val fechaCreacion: String, // Puedes cambiar a Date si haces parsing
    val usuarioId: Int
) : Serializable
