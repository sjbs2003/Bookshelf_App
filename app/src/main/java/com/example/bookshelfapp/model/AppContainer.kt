package com.example.bookshelfapp.model

import com.example.bookshelfapp.network.RetrofitClient
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

object RetrofitBuilder {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    fun <T> buildService(
        serviceClass: Class<T>,
        baseUrl: String,
        retrofitClient: RetrofitClient
    ): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(retrofitClient.client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(serviceClass)
    }
}