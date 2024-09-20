package com.example.bookshelfapp.data

import com.example.bookshelfapp.model.ApiResponse
import com.example.bookshelfapp.model.Item
import com.example.bookshelfapp.network.ApiService

interface Repository {
    suspend fun searchBooks(query: String, searchType: String, maxResults: Int, startIndex: Int): ApiResponse
    suspend fun getBookDetails(volumeId: String): Item
}

class NetworkRepository(
    private val apiService: ApiService
) : Repository {
    override suspend fun searchBooks(query: String, searchType: String, maxResults: Int, startIndex: Int): ApiResponse =
        apiService.searchBooks("$searchType:$query", maxResults, startIndex)

    override suspend fun getBookDetails(volumeId: String): Item =
        apiService.getBookDetails(volumeId)
}
