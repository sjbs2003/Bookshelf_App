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
import okio.IOException
import retrofit2.HttpException

sealed interface DetailUiState {
    data class Success(val bookDetail: Item) : DetailUiState
    data object Loading : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(
    private val repository: Repository,
    private val volumeId: String,
    private val isGoogleBooks: Boolean
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
                when (val result = repository.getBookDetails(volumeId, isGoogleBooks)) {
                    is NetworkResult.Success -> {
                        _uiState.value = DetailUiState.Success(result.data)
                    }
                    is NetworkResult.Error -> {
                        val errorMessage = when (val exception = result.exception) {
                            is IOException -> "Network Error: Please check your connection"
                            is HttpException -> "Server Error: ${exception.code()}"
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun retry() {
        getBookDetails()
    }
}