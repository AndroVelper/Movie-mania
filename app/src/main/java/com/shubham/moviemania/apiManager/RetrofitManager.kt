package com.shubham.moviemania.apiManager

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitManager {
    private const val BASE_URL = "https://www.omdbapi.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiCalls by lazy {
        retrofit.create(ApiCalls::class.java)
    }
}


