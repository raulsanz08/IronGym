package com.example.irongym.entity

data class OpenAiRequest(
    val model: String,
    val messages: List<OpenAiRequestMessage>
)
