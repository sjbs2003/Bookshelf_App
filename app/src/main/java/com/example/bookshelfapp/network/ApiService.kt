package com.example.bookshelfapp.network

import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "Add the API KEY"

interface ApiService{
    @GET("volumes")
    suspend fun getBooks(
        @Query("q") searchQuery: String,
        @Query("key") apikey: String = API_KEY,
    ): ApiService
}