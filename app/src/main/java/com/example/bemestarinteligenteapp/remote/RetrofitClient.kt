package com.example.bemestarinteligenteapp.remote

import com.example.bemestarinteligenteapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val BASE_URL = "https://openrouter.ai/api/v1/"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original: Request = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Authorization", "Bearer ${BuildConfig.API_KEY}") // Ou use outro header se necessário
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        .build()

    val instance: DeepSeekApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)  // 🔥 Agora usa um client com Interceptors
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DeepSeekApiService::class.java)
    }
}