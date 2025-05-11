package com.example.irongym.entity

data class Desafio(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val imagenUrl: String,
    var activo: Boolean, // 🔥 El estado del CheckBox
    var cambiado: Boolean = false // 🔥 NUEVO: Para saber si ha cambiado o no
)
