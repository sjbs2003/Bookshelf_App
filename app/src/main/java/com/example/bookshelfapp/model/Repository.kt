package com.example.bookshelfapp.model

import com.example.bookshelfapp.network.GoogleBooksApiService
import com.example.bookshelfapp.network.OpenLibraryApiService
import io.reactivex.rxjava3.core.Single
import okio.IOException
import retrofit2.HttpException

data class CombinedSearchResult(
    val googleBooks: List<Item>,
    val openLibraryBooks: List<OpenLibraryBook>,
    val totalItems: Int
)

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Throwable) : NetworkResult<Nothing>()
}

interface Repository {
    fun searchBooks(
        query: String,
        searchType: String,
        maxResults: Int,
        startIndex: Int
    ): Single<NetworkResult<CombinedSearchResult>>
    suspend fun getBookDetails(volumeId: String): NetworkResult<Item>
    suspend fun getBookshelves(userId: String): NetworkResult<BookshelfResponse>
    suspend fun getBookshelfVolumes(userId: String, shelf: String): NetworkResult<ApiResponse>
}

class NetworkRepository(
    private val googleBooksApi: GoogleBooksApiService,
    private val openLibraryApi: OpenLibraryApiService
) : Repository {

    override fun searchBooks(
        query: String,
        searchType: String,
        maxResults: Int,
        startIndex: Int
    ): Single<NetworkResult<CombinedSearchResult>> {
        return Single.zip(
            googleBooksApi.searchBooks("$searchType:$query", maxResults, startIndex)
                .onErrorReturn { ApiResponse("kind", 0, emptyList()) },
            openLibraryApi.searchBooks(query, page = (startIndex / maxResults) + 1, limit = maxResults)
                .onErrorReturn { OpenLibraryResponse(emptyList(), 0, 0, false) }
        ) { googleResponse, openLibraryResponse ->
            try {
                NetworkResult.Success(
                    CombinedSearchResult(
                        googleBooks = googleResponse.items,
                        openLibraryBooks = openLibraryResponse.docs,
                        totalItems = googleResponse.totalItems + openLibraryResponse.numFound
                    )
                )
            } catch (e: Exception) {
                NetworkResult.Error(e)
            }
        }.onErrorReturn { error ->
            NetworkResult.Error(error)
        }
    }

    override suspend fun getBookDetails(volumeId: String): NetworkResult<Item> {
        return try {
            val response = googleBooksApi.getBookDetails(volumeId).blockingGet()
            NetworkResult.Success(response)
        } catch (e: Exception) {
            when (e) {
                is IOException -> NetworkResult.Error(
                    IOException("Network error occurred. Please check your connection.")
                )
                is HttpException -> NetworkResult.Error(
                    Exception("HTTP ${e.code()}: ${e.message()}")
                )
                else -> NetworkResult.Error(
                    Exception("An unexpected error occurred: ${e.message}")
                )
            }
        }
    }

    override suspend fun getBookshelves(userId: String): NetworkResult<BookshelfResponse> {
        return try {
            val response = googleBooksApi.getBookshelves(userId).blockingGet()
            NetworkResult.Success(response)
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun getBookshelfVolumes(
        userId: String,
        shelf: String
    ): NetworkResult<ApiResponse> {
        return try {
            val response = googleBooksApi.getBookshelfVolumes(userId, shelf).blockingGet()
            NetworkResult.Success(response)
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun handleError(error: Throwable): NetworkResult.Error {
        return when (error) {
            is IOException -> NetworkResult.Error(
                IOException("Network error occurred. Please check your connection.")
            )
            is HttpException -> NetworkResult.Error(
                Exception("HTTP ${error.code()}: ${error.message()}")
            )
            else -> NetworkResult.Error(
                Exception("An unexpected error occurred: ${error.message}")
            )
        }
    }
}
