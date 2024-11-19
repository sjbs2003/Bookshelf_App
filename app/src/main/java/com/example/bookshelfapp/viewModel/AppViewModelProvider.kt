package com.example.bookshelfapp.viewModel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelfapp.BookShelfApplication

object AppViewModelProvider {

    val Factory = viewModelFactory {

        initializer {
            val application = (this[APPLICATION_KEY] as BookShelfApplication)
            val repository = application.container.repository
            SearchViewModel(repository = repository)
        }

        initializer {
            val application = (this[APPLICATION_KEY] as BookShelfApplication)
            val repository = application.container.repository
            val savedStateHandle = this.createSavedStateHandle()

            // Get the volumeId from navigation arguments
            val volumeId = checkNotNull(savedStateHandle.get<String>("volumeId")) {
                "volumeId parameter wasn't found. Please ensure you're passing a volumeId"
            }
            DetailViewModel(repository = repository, volumeId = volumeId)
        }
    }
}