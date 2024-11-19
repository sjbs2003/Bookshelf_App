package com.example.bookshelfapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelfapp.model.Item
import com.example.bookshelfapp.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

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

    init {
        getBookDetails()
    }

    private fun getBookDetails() {
        viewModelScope.launch {
            try {
                _uiState.value = DetailUiState.Loading
                val bookDetail = repository.getBookDetails(volumeId)
                _uiState.value = DetailUiState.Success(bookDetail)
            } catch (e: IOException) {
                _uiState.value = DetailUiState.Error("Network Error: ${e.message}")
            } catch (e: HttpException) {
                _uiState.value = DetailUiState.Error("HTTP error: ${e.code()} ${e.message()}")
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }
}