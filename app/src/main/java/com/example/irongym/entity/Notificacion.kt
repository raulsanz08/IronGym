package com.example.irongym.entity

import java.io.Serializable

data class Notificacion(
    val id: Int,
    val descripcion: String,
    var activo: Boolean,
    val fechaCreacion: String,
    val usuarioId: Int
) : Serializable
