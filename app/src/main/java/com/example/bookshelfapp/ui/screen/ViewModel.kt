package com.example.bookshelfapp.ui.screen

import com.example.bookshelfapp.model.ApiResponse

sealed interface BookShelfUState{
    data class Success(val item: ApiResponse): BookShelfUState
    object Loading: BookShelfUState
    object Error: BookShelfUState
}