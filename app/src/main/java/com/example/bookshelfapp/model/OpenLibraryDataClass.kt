package com.example.bookshelfapp.model

import kotlinx.serialization.SerialName
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

@Serializable
data class OpenLibraryWork(
    val key: String,
    val title: String,
    val description: Description? = null,
    val authors: List<Author>? = null,
    @SerialName("first_publish_date")
    val firstPublishDate: String? = null,
    val covers: List<Int>? = null,
    val subjects: List<String>? = null,
    @SerialName("subject_places")
    val subjectPlaces: List<String>? = null,
    @SerialName("subject_times")
    val subjectTimes: List<String>? = null,
    @SerialName("subject_people")
    val subjectPeople: List<String>? = null,
    val excerpts: List<Excerpt>? = null,
    val links: List<Link>? = null
) {
    @Serializable
    data class Description(
        val type: String? = null,
        val value: String
    )

    @Serializable
    data class Author(
        val author: AuthorReference,
        val type: TypeReference
    )

    @Serializable
    data class AuthorReference(
        val key: String
    )

    @Serializable
    data class TypeReference(
        val key: String
    )

    @Serializable
    data class Excerpt(
        val excerpt: String,
        val comment: String? = null,
        val author: AuthorReference? = null
    )

    @Serializable
    data class Link(
        val url: String,
        val title: String,
        val type: TypeReference? = null
    )
}

// Helper extension to handle cases where description might be either a string or an object
fun OpenLibraryWork.getDescription(): String {
    return when {
        description?.value != null -> description.value
        description?.type != null -> "Description type: ${description.type}"
        else -> "No description available"
    }
}