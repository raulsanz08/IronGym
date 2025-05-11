package com.example.irongym.entity

import com.google.gson.annotations.SerializedName

data class EstadisticasResponse(
    @SerializedName("ritmo_cardiaco")
    val ritmoCardiaco: Int,

    val fuerza: Int,
    val peso: Int,
    val logros: Int,
    val disciplina: Int,
    val altura: Int,
    val resistencia: Int
)
