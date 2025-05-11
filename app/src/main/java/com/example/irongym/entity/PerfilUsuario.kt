package com.example.irongym.entity

data class PerfilUsuario(
    val nombre: String,
    val peso: Float,
    val altura: Int,
    val avatar_url: String? = null // Opcional, ya que puede ser nulo
)
