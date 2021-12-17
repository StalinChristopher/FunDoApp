package com.bl.todo.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroFitClient {

    private const val BASE_URL = "https://firestore.googleapis.com/v1/"
     fun createRetroFit(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
     }
}