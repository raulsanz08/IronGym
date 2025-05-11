package com.example.irongym.entity

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val usuario_id: Int? = null
)
