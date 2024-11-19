package com.example.bookshelfapp.viewModel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
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
    }
}