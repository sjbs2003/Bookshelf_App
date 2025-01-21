package com.example.bookshelfapp.network

import com.example.bookshelfapp.model.RetrofitBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.concurrent.TimeUnit

class RetrofitClient(val client: OkHttpClient)

object ApiServiceFactory {
    private const val TIMEOUT_SECONDS = 30L
    private const val MAX_RETRIES = 3
    private const val GOOGLE_BOOKS_BASE_URL = "https://www.googleapis.com/books/v1/"
    private const val OPEN_LIBRARY_BASE_URL = "https://openlibrary.org/"

    private fun createBaseClient(): RetrofitClient {
        val client = OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor(createErrorInterceptor())
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        return RetrofitClient(client)
    }

    private fun createLoggingInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            println("Making request to: ${request.url}")
            val response = chain.proceed(request)
            println("Received response from: ${request.url}")
            response
        }
    }

    private fun createErrorInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                // Clone the response before throwing the exception since response.body can only be consumed once
                val errorResponse = response.newBuilder().body(
                    (errorBody ?: "").toResponseBody(response.body?.contentType())
                ).build()

                throw ApiException(
                    code = errorResponse.code,
                    message = errorBody ?: errorResponse.message
                )
            }
            response
        }
    }

    fun createGoogleBooksService(
        baseUrl: String = GOOGLE_BOOKS_BASE_URL
    ): GoogleBooksApiService {
        return RetrofitBuilder.buildService(
            serviceClass = GoogleBooksApiService::class.java,
            baseUrl = baseUrl,
            retrofitClient = createBaseClient()
        )
    }

    fun createOpenLibraryService(
        baseUrl: String = OPEN_LIBRARY_BASE_URL
    ): OpenLibraryApiService {
        return RetrofitBuilder.buildService(
            serviceClass = OpenLibraryApiService::class.java,
            baseUrl = baseUrl,
            retrofitClient = createBaseClient()
        )
    }
}

class ApiException(
    val code: Int,
    override val message: String
) : Exception("HTTP $code: $message")