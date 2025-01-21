package com.example.bookshelfapp.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelfapp.model.*
import com.example.bookshelfapp.network.OpenLibraryApiService
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    data class Success(val items: List<Item>) : SearchUiState
    data object Loading : SearchUiState
    data class Error(val message: String) : SearchUiState
}

class SearchViewModel(
    private val repository: Repository
) : ViewModel() {
    var userInput: String by mutableStateOf("")
        private set

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Success(emptyList()))
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    var searchType: String by mutableStateOf("title")
        private set

    var useGoogleBooks: Boolean by mutableStateOf(true)
        private set

    private var searchJob: Job? = null
    private var currentPage: Int = 0
    private val itemsPerPage = 10
    private val compositeDisposable = CompositeDisposable()

    fun toggleApiSource() {
        useGoogleBooks = !useGoogleBooks
        if (userInput.isNotBlank()) {
            searchBooks()
        }
    }

    fun updateSearchType(newType: String) {
        searchType = newType
        if (userInput.isNotBlank()) {
            searchBooks()
        }
    }

    fun updateUserInput(input: String) {
        userInput = input
        searchJob?.cancel()

        if (input.isBlank()) {
            clearUserInput()
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // Debounce delay
            searchBooks()
        }
    }

    fun clearUserInput() {
        userInput = ""
        _searchUiState.value = SearchUiState.Success(emptyList())
        currentPage = 0
        searchJob?.cancel()
    }

    private fun searchBooks() {
        if (userInput.isBlank()) {
            _searchUiState.value = SearchUiState.Success(emptyList())
            return
        }

        _searchUiState.value = SearchUiState.Loading
        currentPage = 0

        repository.searchBooks(
            query = userInput,
            searchType = searchType,
            maxResults = itemsPerPage,
            startIndex = currentPage * itemsPerPage
        )
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val items = if (useGoogleBooks) {
                            result.data.googleBooks
                        } else {
                            result.data.openLibraryBooks.mapToGoogleBooksFormat()
                        }
                        _searchUiState.value = SearchUiState.Success(items)
                    }
                    is NetworkResult.Error -> {
                        _searchUiState.value = SearchUiState.Error(
                            result.exception.message ?: "Unknown error occurred"
                        )
                    }
                }
            }, { error ->
                _searchUiState.value = SearchUiState.Error(
                    error.message ?: "Unknown error occurred"
                )
            })
            .addTo(compositeDisposable)
    }

    private fun List<OpenLibraryBook>.mapToGoogleBooksFormat(): List<Item> {
        return map { book ->
            Item(
                id = book.key,
                volumeInfo = VolumeInfo(
                    title = book.title,
                    authors = book.author_name ?: listOf("Author not available"),
                    publishedDate = book.first_publish_year?.toString()
                        ?: "Publication date not available",
                    imageLinks = ImageLinks(
                        thumbnail = book.cover_i?.let { coverId ->
                            OpenLibraryApiService.getCoverUrl(coverId)
                        } ?: "Image not available"
                    )
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}