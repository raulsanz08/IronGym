package com.example.irongym.retrofit

import android.content.Context
import com.example.irongym.api.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.1.147:8000/"
    private lateinit var retrofit: Retrofit


    fun initialize(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        val client = OkHttpClient.Builder().apply {
            if (token != null) {
                addInterceptor(AuthInterceptor(token))
            }
        }.build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        if (!::retrofit.isInitialized) {
            throw IllegalStateException("RetrofitInstance not initialized. Call initialize(context) first.")
        }
        retrofit.create(ApiService::class.java)
    }

}



