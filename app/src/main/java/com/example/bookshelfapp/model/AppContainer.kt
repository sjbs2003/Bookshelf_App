package com.example.bookshelfapp.model

import com.example.bookshelfapp.network.ApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val repository: Repository
}

class DefaultAppContainer: AppContainer {

    private val baseurl = "https://www.googleapis.com/books/v1/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseurl)
        .build()

    private val retrofitService: ApiService by lazy {
        retrofit.create(ApiService :: class.java)
    }

    override val repository: Repository by lazy {
        NetworkRepository(retrofitService)
    }
}