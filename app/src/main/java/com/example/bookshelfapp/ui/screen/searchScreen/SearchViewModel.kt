package com.example.bookshelfapp.ui.screen.searchScreen

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

sealed interface SearchUiState {
    data class Success(val items: List<Item>) : SearchUiState
    data object Loading : SearchUiState
    data class Error(val message: String) : SearchUiState
}

class SearchViewModel(private val repository: Repository) : ViewModel() {
    var userInput: String by mutableStateOf("")
        private set

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    var searchType: String by mutableStateOf("intitle") // Default search type

    val searchResults: List<Item>
        get() = (_searchUiState.value as? SearchUiState.Success)?.items ?: emptyList()


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
            _searchUiState.value = SearchUiState.Error("Search query cannot be empty")
            return
        }
        _searchUiState.value = SearchUiState.Loading
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
                val response = repository.searchBooks(
                    userInput,
                    searchType,
                    itemsPerPage,
                    currentPage * itemsPerPage
                )
                val currentItems = (_searchUiState.value as? SearchUiState.Success)?.items ?: emptyList()
                _searchUiState.value = SearchUiState.Success(currentItems + response.items)
            } catch (e: IOException) {
                _searchUiState.value = SearchUiState.Error("Network Error: ${e.message}")
            } catch (e: HttpException) {
                _searchUiState.value = SearchUiState.Error("HTTP error: ${e.code()} ${e.message()}")
            } catch (e: Exception) {
                _searchUiState.value = SearchUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BookShelfApplication)
                val repository = application.container.repository
                SearchViewModel(repository = repository)
            }
        }
    }
}
