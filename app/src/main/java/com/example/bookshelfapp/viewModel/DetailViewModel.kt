package com.example.bookshelfapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelfapp.model.*
import com.example.bookshelfapp.network.OpenLibraryApiService
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    data class Success(val bookDetail: Item) : DetailUiState
    data object Loading : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(
    private val repository: Repository,
    private val volumeId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val compositeDisposable = CompositeDisposable()

    init {
        getBookDetails()
    }

    private fun getBookDetails() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading

            try {
                when (val result = repository.getBookDetails(volumeId)) {
                    is NetworkResult.Success -> {
                        _uiState.value = DetailUiState.Success(result.data)
                    }
                    is NetworkResult.Error -> {
                        val errorMessage = when (val exception = result.exception) {
                            is java.io.IOException -> "Network Error: Please check your connection"
                            is retrofit2.HttpException -> "Server Error: ${exception.code()}"
                            else -> exception.message ?: "An unexpected error occurred"
                        }
                        _uiState.value = DetailUiState.Error(errorMessage)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(
                    e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    // Helper function to map OpenLibrary work to Google Books format if needed
    private fun mapOpenLibraryToGoogleFormat(work: OpenLibraryWork): Item {
        return Item(
            id = work.key,
            volumeInfo = VolumeInfo(
                title = work.title,
                authors = work.authors?.map { it.author.key } ?: listOf("Author not available"),
                publishedDate = work.firstPublishDate ?: "Publication date not available",
                description = work.getDescription(),
                imageLinks = ImageLinks(
                    thumbnail = work.covers?.firstOrNull()?.let { coverId ->
                        OpenLibraryApiService.getCoverUrl(coverId)
                    } ?: "Image not available"
                ),
                categories = work.subjects,
                language = null
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun retry() {
        getBookDetails()
    }
}