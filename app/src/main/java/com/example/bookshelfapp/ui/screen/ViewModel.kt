package com.example.bookshelfapp.ui.screen

import android.util.Log
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
import com.example.bookshelfapp.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

sealed interface BookShelfUiState {
    data class Success(val items: List<Item>) : BookShelfUiState
    data object Loading : BookShelfUiState
    data class Error(val message: String) : BookShelfUiState
}

class BookShelfViewModel(private val bookshelfRepository: Repository) : ViewModel() {
    var userInput: String by mutableStateOf("")
        private set

    private val _bookShelfUiState = MutableStateFlow<BookShelfUiState>(BookShelfUiState.Loading)
    val bookShelfUiState: StateFlow<BookShelfUiState> = _bookShelfUiState.asStateFlow()

    var searchType: String by mutableStateOf("intitle") // Default search type

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
                handleError("Network Error", e)
            } catch (e: HttpException) {
                handleError("HTTP error: ${e.code()} ${e.message()}", e)
            } catch (e: Exception) {
                handleError("Unexpected Error", e)
            }
        }
    }

    private fun handleError(message: String, exception: Exception){
        Log.e("BookShelfViewModel", message, exception)
        _bookShelfUiState.value = BookShelfUiState.Error("$message: ${exception.message}")
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
