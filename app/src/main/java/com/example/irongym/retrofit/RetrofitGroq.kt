package com.example.irongym.retrofit

import com.example.irongym.api.ApiServiceGroq
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitGroq {
    private const val BASE_URL = "https://api.groq.com/openai/"

    private val retrofit: Retrofit by lazy {
        val client = OkHttpClient.Builder().build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiServiceGroq by lazy {
        retrofit.create(ApiServiceGroq::class.java)
    }
}
