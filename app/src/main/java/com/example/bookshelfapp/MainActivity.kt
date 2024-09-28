package com.example.bookshelfapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookshelfapp.ui.screen.searchScreen.SearchScreen
import com.example.bookshelfapp.ui.screen.searchScreen.SearchViewModel
import com.example.bookshelfapp.ui.theme.BookshelfAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookshelfAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: SearchViewModel = viewModel(factory = SearchViewModel.factory)

                    SearchScreen(
                        viewModel = viewModel,
                        onSearch = { viewModel.searchBooks() }
                    )
                }
            }
        }
    }
}

