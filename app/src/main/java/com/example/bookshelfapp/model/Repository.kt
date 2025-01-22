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
    suspend fun getBookDetails(volumeId: String, isGoogleBooks: Boolean): NetworkResult<Item>
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

    override suspend fun getBookDetails(volumeId: String, isGoogleBooks: Boolean): NetworkResult<Item> {
        return try {
            if (isGoogleBooks) {
                val response = googleBooksApi.getBookDetails(volumeId).blockingGet()
                NetworkResult.Success(response)
            } else {
                val workId = volumeId.removePrefix("/works/")
                try {
                    val response = openLibraryApi.getWorkDetails(workId).blockingGet()
                    NetworkResult.Success(mapOpenLibraryWorkToItem(response))
                } catch (e: Exception) {
                    // Fallback to search if work details fail
                    val searchResponse = openLibraryApi.searchBooks(workId, limit = 1).blockingGet()
                    if (searchResponse.docs.isNotEmpty()) {
                        NetworkResult.Success(mapOpenLibraryBookToItem(searchResponse.docs.first()))
                    } else {
                        NetworkResult.Error(Exception("Book details not found"))
                    }
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    private fun mapOpenLibraryWorkToItem(work: OpenLibraryWork): Item {
        return Item(
            id = work.key,
            volumeInfo = VolumeInfo(
                title = work.title,
                authors = work.authors?.map { author ->
                    OpenLibraryApiService.getAuthorName(author.author.key)
                } ?: listOf("Author not available"),
                publishedDate = work.firstPublishDate ?: "Publication date not available",
                description = when (work.description) {
                    is OpenLibraryWork.Description -> work.description.value
                    null -> "No description available"
                    else -> "Description format not supported"
                },
                publisher = null,
                pageCount = null,
                categories = work.subjects,
                imageLinks = ImageLinks(
                    thumbnail = work.covers?.firstOrNull()?.let { coverId ->
                        OpenLibraryApiService.getCoverUrl(coverId)
                    } ?: "Image not available"
                ),
                language = null,
                industryIdentifiers = null
            )
        )
    }

    private fun mapOpenLibraryBookToItem(book: OpenLibraryBook): Item {
        return Item(
            id = book.key,
            volumeInfo = VolumeInfo(
                title = book.title,
                authors = book.author_name ?: listOf("Author not available"),
                publishedDate = book.first_publish_year?.toString() ?: "Publication date not available",
                description = "Description not available",  // OpenLibraryBook doesn't contain description
                publisher = book.publisher?.firstOrNull(),
                pageCount = null,
                categories = null,
                imageLinks = ImageLinks(
                    thumbnail = book.cover_i?.let { coverId ->
                        OpenLibraryApiService.getCoverUrl(coverId)
                    } ?: "Image not available"
                ),
                language = book.language?.firstOrNull(),
                industryIdentifiers = book.isbn?.map { isbn ->
                    IndustryIdentifier(
                        type = if (isbn.length == 13) "ISBN_13" else "ISBN_10",
                        identifier = isbn
                    )
                }
            ),
            saleInfo = SaleInfo(
                country = "US",
                saleability = "NOT_FOR_SALE",
                isEbook = false
            )
        )
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
