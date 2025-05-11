package com.example.irongym.entity

data class OpenAiRequestMessage(
    val role: String,   // Puede ser "user", "assistant" o "system"
    val content: String
)
