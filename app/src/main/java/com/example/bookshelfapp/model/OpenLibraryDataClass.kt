package com.example.bookshelfapp.model

import kotlinx.serialization.Serializable

@Serializable
data class OpenLibraryResponse(
    val docs: List<OpenLibraryBook>,
    val numFound: Int,
    val start: Int,
    val numFoundExact: Boolean
)

@Serializable
data class OpenLibraryBook(
    val key: String,
    val title: String,
    val author_name: List<String>? = null,
    val first_publish_year: Int? = null,
    val cover_i: Int? = null,
    val isbn: List<String>? = null,
    val publisher: List<String>? = null,
    val language: List<String>? = null,
    val availability: OpenLibraryAvailability? = null
)

@Serializable
data class OpenLibraryAvailability(
    val status: String,
    val available_to_borrow: Boolean,
    val available_to_browse: Boolean
)
