package com.example.irongym.entity

data class DiaDieta(
    val id: Int,
    val dieta: Int, // ID de la dieta relacionada
    val dia: String,
    val descripcion: String,
    val imagenUrl: String
)
