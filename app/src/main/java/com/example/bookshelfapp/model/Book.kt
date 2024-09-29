package com.example.bookshelfapp.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val kind: String,
    val totalItems: Int,
    val items: List<Item>
)

@Serializable
data class Item(
    val id: String,
    val volumeInfo: VolumeInfo,
    val saleInfo: SaleInfo? = null,
    val accessInfo: AccessInfo? = null
)

@Serializable
data class VolumeInfo(
    val title: String = "Title not available",
    val authors: List<String> = listOf("Author not available"),
    val publishedDate: String = "Publication date not available",
    val description: String = "Description not available",
    val pageCount: Int? = null,
    val categories: List<String>? = null,
    val imageLinks: ImageLinks = ImageLinks(thumbnail = "Image not available"),
    val language: String? = null
)

@Serializable
data class ImageLinks(
    val smallThumbnail: String? = null,
    val thumbnail: String
){

}

@Serializable
data class SaleInfo(
    val country: String,
    val saleability: String,
    val isEbook: Boolean
)

@Serializable
data class AccessInfo(
    val country: String,
    val viewability: String,
    val embeddable: Boolean,
    val publicDomain: Boolean,
    val textToSpeechPermission: String,
    val epub: EpubInfo,
    val pdf: PdfInfo,
    val webReaderLink: String,
    val accessViewStatus: String
)

@Serializable
data class EpubInfo(
    val isAvailable: Boolean,
    val acsTokenLink: String? = null
)

@Serializable
data class PdfInfo(
    val isAvailable: Boolean,
    val acsTokenLink: String? = null
)

@Serializable
data class BookshelfResponse(
    val kind: String,
    val items: List<Bookshelf>
){
    @Serializable
    data class Bookshelf(
        val kind: String,
        val id: Int,
        val selfLink: String,
        val title: String,
        val access: String,
        val updated: String,
        val created: String,
        val volumeCount: Int,
        val volumesLastUpdated: String
    )

}
