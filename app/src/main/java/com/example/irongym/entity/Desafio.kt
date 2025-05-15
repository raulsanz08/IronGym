package com.example.irongym.entity

data class Desafio(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val imagenUrl: String,
    var activo: Boolean,
    var cambiado: Boolean = false
)
