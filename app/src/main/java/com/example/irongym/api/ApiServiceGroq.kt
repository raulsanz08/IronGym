package com.example.irongym.api

import com.example.irongym.entity.OpenAiRequest
import com.example.irongym.entity.OpenAiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiServiceGroq {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer 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"
    )
    @POST("v1/chat/completions")
    fun sendChatMessage(@Body body: OpenAiRequest): Call<OpenAiResponse>
}
