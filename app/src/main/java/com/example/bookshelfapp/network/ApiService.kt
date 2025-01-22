package com.example.bookshelfapp.network

import com.example.bookshelfapp.model.*
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*
import java.util.Locale

interface BaseApiService {
    fun handleError(error: Throwable): Single<ApiResponse>
}

// Google Books API Service with improved error handling and retry logic
interface GoogleBooksApiService : BaseApiService {
    @GET("volumes")
    fun searchBooks(
        @Query("q") searchQuery: String,
        @Query("maxResults") maxResults: Int = 10,
        @Query("startIndex") startIndex: Int = 0,
        @Query("orderBy") orderBy: String = "relevance",
        @Query("printType") printType: String = "all"
    ): Single<ApiResponse>

    @GET("volumes/{volumeId}")
    fun getBookDetails(
        @Path("volumeId") volumeId: String
    ): Single<Item>

    @GET("users/{userId}/bookshelves")
    fun getBookshelves(
        @Path("userId") userId: String
    ): Single<BookshelfResponse>

    @GET("users/{userId}/bookshelves/{shelf}/volumes")
    fun getBookshelfVolumes(
        @Path("userId") userId: String,
        @Path("shelf") shelf: String
    ): Single<ApiResponse>

    @POST("mylibrary/bookshelves/{shelf}/addVolume")
    fun addToBookshelf(
        @Path("shelf") shelf: String,
        @Query("volumeId") volumeId: String
    ): Single<Unit>

    @POST("mylibrary/bookshelves/{shelf}/removeVolume")
    fun removeFromBookshelf(
        @Path("shelf") shelf: String,
        @Query("volumeId") volumeId: String
    ): Single<Unit>
}

// Open Library API Service with improved error handling and retry logic
interface OpenLibraryApiService : BaseApiService {
    @GET("search.json")
    fun searchBooks(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("fields") fields: String = "key,title,author_name,first_publish_year,cover_i,isbn,publisher,language,availability"
    ): Single<OpenLibraryResponse>

    @GET("works/{workId}.json")
    fun getWorkDetails(
        @Path("workId") workId: String
    ): Single<OpenLibraryWork>

    @GET("books/{isbn}.json")
    fun getBookByIsbn(
        @Path("isbn") isbn: String
    ): Single<OpenLibraryBook>

    companion object {
        private const val COVERS_BASE_URL = "https://covers.openlibrary.org/b"
        private const val MEDIUM_SIZE = "M"

        fun getCoverUrl(coverId: Int, size: String = MEDIUM_SIZE): String {
            // Use HTTPS by default and ensure proper URL formatting
            return "https://covers.openlibrary.org/b/id/$coverId-$size.jpg"
        }

        fun getAuthorName(authorKey: String): String {
            // Remove the '/authors/' prefix and format the name
            return authorKey.removePrefix("/authors/").replace("_", " ")
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        }
    }
}
