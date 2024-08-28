package com.example.bookshelfapp.data

import com.example.bookshelfapp.network.ApiService

interface Repository{
    suspend fun getBooks(query: String): ApiService
}

class NetworkRepository(
    private val apiService: ApiService
): Repository{
    override suspend fun getBooks(query: String): ApiService = apiService.getBooks(query)
}
