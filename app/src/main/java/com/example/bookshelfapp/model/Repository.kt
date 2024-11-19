package com.example.bookshelfapp.model

import com.example.bookshelfapp.network.ApiService

interface Repository {
    suspend fun searchBooks(query: String, searchType: String, maxResults: Int, startIndex: Int): ApiResponse
    suspend fun getBookDetails(volumeId: String): Item
    suspend fun getBookshelves(userId: String):BookshelfResponse
    suspend fun getBookshelfVolumes(userId: String, shelf: String): ApiResponse
    suspend fun addToBookshelf(shelf: String, volumeId: String)
    suspend fun removeFromBookshelf(shelf: String, volumeId: String)
}

class NetworkRepository(
    private val apiService: ApiService
) : Repository {
    override suspend fun searchBooks(query: String, searchType: String, maxResults: Int, startIndex: Int): ApiResponse =
        apiService.searchBooks("$searchType:$query", maxResults, startIndex)

    override suspend fun getBookDetails(volumeId: String): Item =
        apiService.getBookDetails(volumeId)

    override suspend fun getBookshelves(userId: String): BookshelfResponse =
        apiService.getBookshelves(userId)

    override suspend fun getBookshelfVolumes(userId: String, shelf: String): ApiResponse =
        apiService.getBookshelfVolumes(userId,shelf)

    override suspend fun addToBookshelf(shelf: String, volumeId: String) =
        apiService.addToBookshelf(shelf, volumeId)

    override suspend fun removeFromBookshelf(shelf: String, volumeId: String) =
        apiService.removeFromBookshelf(shelf, volumeId)
}
