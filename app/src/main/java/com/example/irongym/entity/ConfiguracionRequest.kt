package com.example.irongym.entity

data class ConfiguracionRequest(
    val email: String,
    val contrasena: String,
    val notificaciones: Boolean,
    val idioma: String
)
