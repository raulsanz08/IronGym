package com.example.irongym.retrofit

import com.example.irongym.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitEscaner {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://world.openfoodfacts.org/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val api: ApiService = retrofit.create(ApiService::class.java)
}
