package com.example.bookshelfapp.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelfapp.model.Item
import com.example.bookshelfapp.model.Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    var searchType: String by mutableStateOf("title")
        private set

    val searchResults: List<Item>
        get() = (_searchUiState.value as? SearchUiState.Success)?.items ?: emptyList()


    private var currentPage: Int = 0
    private val itemsPerPage = 10
    private var searchJob: Job? = null

    fun updateSearchType(newType: String) {
        searchType = newType
        if (userInput.isNotBlank()){
            searchBooks()
        }
    }

    fun updateUserInput(input: String) {
        userInput = input
        searchJob?.cancel()

        if (input.isBlank()){
            clearUserInput()
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)
            searchBooks()
        }
    }

    fun clearUserInput() {
        userInput = ""
        _searchUiState.value = SearchUiState.Success(emptyList())
        currentPage = 0
        searchJob?.cancel()
    }

    fun searchBooks() {
        if (userInput.isBlank()) {
            _searchUiState.value = SearchUiState.Success(emptyList())
            return
        }
        _searchUiState.value = SearchUiState.Loading
        currentPage = 0
        fetchBooks()
    }


    private fun fetchBooks() {
        viewModelScope.launch {
            try {
                val query = when(searchType) {
                    "title" -> "intitle: $userInput"
                    "subject" -> "insubject: $userInput"
                    "author" -> "inauthor: $userInput"
                    else -> userInput
                }

                val response = repository.searchBooks(
                    userInput,
                    searchType,
                    itemsPerPage,
                    currentPage * itemsPerPage
                )
                _searchUiState.value = SearchUiState.Success(response.items)
            } catch (e: IOException) {
                _searchUiState.value = SearchUiState.Error("Network Error: ${e.message}")
            } catch (e: HttpException) {
                _searchUiState.value = SearchUiState.Error("HTTP error: ${e.code()} ${e.message()}")
            } catch (e: Exception) {
                _searchUiState.value = SearchUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }
}
