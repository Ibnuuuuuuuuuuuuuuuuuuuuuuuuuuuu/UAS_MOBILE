package com.example.kopipedia.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: CoffeeApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.sampleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(CoffeeApiService::class.java)
    }
}