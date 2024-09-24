package com.example.bookshelfapp.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelfapp.BookShelfApplication
import com.example.bookshelfapp.data.Repository
import com.example.bookshelfapp.model.BookshelfResponse
import com.example.bookshelfapp.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

sealed interface BookShelfUiState { // for searching books
    data class Success(val items: List<Item>) : BookShelfUiState
    data object Loading : BookShelfUiState
    data class Error(val message: String) : BookShelfUiState
    data object OperationSuccess : BookShelfUiState
}

sealed interface BookshelvesUiState{ // to handle the state of bookshelves
    data class Success(val bookshelves: List<BookshelfResponse.Bookshelf>) : BookshelvesUiState
    data object Loading: BookshelvesUiState
    data class Error(val message: String): BookshelvesUiState
}

sealed class BookDetailsState {
    data object Loading : BookDetailsState()
    data class Success(val bookDetails: Item) : BookDetailsState()
    data class Error(val message: String) : BookDetailsState()
}

class BookShelfViewModel(private val bookshelfRepository: Repository) : ViewModel() {
    var userInput: String by mutableStateOf("")
        private set

    private val _bookShelfUiState = MutableStateFlow<BookShelfUiState>(BookShelfUiState.Loading)
    val bookShelfUiState: StateFlow<BookShelfUiState> = _bookShelfUiState.asStateFlow()

    private val _bookshelvesUiState = MutableStateFlow<BookshelvesUiState>(BookshelvesUiState.Loading)
    val bookshelvesUiState: StateFlow<BookshelvesUiState> = _bookshelvesUiState.asStateFlow()

    var searchType: String by mutableStateOf("intitle") // Default search type
        private set

    private var currentPage: Int = 0
    private val itemsPerPage = 10

    fun updateUserInput(input: String) {
        userInput = input
    }

    fun clearUserInput() {
        userInput = ""
    }

    fun searchBooks() {
        if (userInput.isBlank()) {
            _bookShelfUiState.value = BookShelfUiState.Error("Search query cannot be empty")
            return
        }
        _bookShelfUiState.value = BookShelfUiState.Loading
        currentPage = 0
        fetchBooks()
    }

    fun loadNextPage() {
        currentPage++
        fetchBooks()
    }

    private fun fetchBooks() {
        viewModelScope.launch {
            try {
                val response = bookshelfRepository.searchBooks(
                    userInput,
                    searchType,
                    itemsPerPage,
                    currentPage * itemsPerPage
                )
                val currentItems = (_bookShelfUiState.value as? BookShelfUiState.Success)?.items ?: emptyList()
                _bookShelfUiState.value = BookShelfUiState.Success(currentItems + response.items)
            } catch (e: IOException) {
                _bookShelfUiState.value = BookShelfUiState.Error("Network Error: ${e.message}")
            } catch (e: HttpException) {
                _bookShelfUiState.value = BookShelfUiState.Error("HTTP error: ${e.code()} ${e.message()}")
            } catch (e: Exception) {
                _bookShelfUiState.value = BookShelfUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }

    private val _bookDetailsState = MutableStateFlow<BookDetailsState>(BookDetailsState.Loading)
    val bookDetailsState: StateFlow<BookDetailsState> = _bookDetailsState.asStateFlow()

    fun getBookDetails(volumeId: String) {
        viewModelScope.launch {
            _bookDetailsState.value = BookDetailsState.Loading
            try {
                val bookDetails = bookshelfRepository.getBookDetails(volumeId)
                _bookDetailsState.value = BookDetailsState.Success(bookDetails)
            } catch (e: IOException) {
                _bookDetailsState.value = BookDetailsState.Error("Network error: ${e.message}")
            } catch (e: HttpException) {
                _bookDetailsState.value = BookDetailsState.Error("HTTP error: ${e.code()} ${e.message()}")
            } catch (e: Exception) {
                _bookDetailsState.value = BookDetailsState.Error("Unexpected error: ${e.message}")
            }
        }
    }

    fun getBookshelves(userId: String){
        viewModelScope.launch {
            _bookshelvesUiState.value = BookshelvesUiState.Loading
            try {
                val bookshelves = bookshelfRepository.getBookshelves(userId)
                _bookshelvesUiState.value = BookshelvesUiState.Success(bookshelves.items)
            }catch (e: Exception){
                _bookshelvesUiState.value = BookshelvesUiState.Error("Error fetching bookshelves: ${e.message}")
            }
        }
    }

    fun getBookshelfVolumes(userId: String, shelf: String){
        viewModelScope.launch {
            _bookShelfUiState.value = BookShelfUiState.Loading
            try{
                val volumes = bookshelfRepository.getBookshelfVolumes(userId,shelf)
                _bookShelfUiState.value = BookShelfUiState.Success(volumes.items)
            }catch (e: Exception){
                _bookShelfUiState.value = BookShelfUiState.Error("Error fetching bookshelf volumes: ${e.message}")
            }
        }
    }

    fun addToBookshelf(shelf: String, volumeId: String){
        viewModelScope.launch {
            try {
                _bookShelfUiState.value = BookShelfUiState.Loading
                bookshelfRepository.addToBookshelf(shelf, volumeId)
                _bookShelfUiState.value = BookShelfUiState.OperationSuccess
            }catch (e:Exception){
                _bookShelfUiState.value = BookShelfUiState.Error("Error adding book to bookshelf: ${e.message}")
            }
        }
    }

    fun removeFromBookshelf(shelf: String, volumeId: String) {
        viewModelScope.launch {
            try {
                bookshelfRepository.removeFromBookshelf(shelf, volumeId)
                // Handle successful removal (e.g., show a success message)
                _bookShelfUiState
            } catch (e: Exception) {
                _bookShelfUiState.value =  BookShelfUiState.Error("Error removing book from bookshelf: ${e.message}")
            }
        }
    }





    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BookShelfApplication)
                val bookshelfRepository = application.container.repository
                BookShelfViewModel(bookshelfRepository = bookshelfRepository)
            }
        }
    }
}
