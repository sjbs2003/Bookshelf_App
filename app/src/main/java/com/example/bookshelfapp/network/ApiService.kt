package com.example.bookshelfapp.network

import com.example.bookshelfapp.model.ApiResponse
import com.example.bookshelfapp.model.BookshelfResponse
import com.example.bookshelfapp.model.Item
import retrofit2.http.*

interface ApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") searchQuery: String,
        @Query("maxResults") maxResults: Int = 10,
        @Query("startIndex") startIndex: Int = 0
    ): ApiResponse

    @GET("volumes/{volumeId}")
    suspend fun getBookDetails(@Path("volumeId") volumeId: String): Item

    @GET("users/{userId}/bookshelves")
    suspend fun getBookshelves(@Path("userId") userId: String): BookshelfResponse

    @GET("users/{userId}/bookshelves/{shelf}/volumes")
    suspend fun getBookshelfVolumes(
        @Path("userId") userId: String,
        @Path("shelf") shelf: String
    ): ApiResponse

    @POST("mylibrary/bookshelves/{shelf}/addVolume")
    suspend fun addToBookshelf(
        @Path("shelf") shelf: String,
        @Query("volumeId") volumeId: String
    )

    @POST("mylibrary/bookshelves/{shelf}/removeVolume")
    suspend fun removeFromBookshelf(
        @Path("shelf") shelf: String,
        @Query("volumeId") volumeId: String
    )
}
