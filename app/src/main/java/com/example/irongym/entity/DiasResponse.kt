package com.example.irongym.entity

data class DiasResponse(
    val success: Boolean,
    val rutina: String,
    val dias: List<DiaEntrenamiento>
)
