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
