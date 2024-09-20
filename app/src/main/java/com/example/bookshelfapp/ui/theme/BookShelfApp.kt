package com.example.bookshelfapp.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.bookshelfapp.ui.screen.BookShelfViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookshelfapp.R
import com.example.bookshelfapp.ui.screen.ResultScreen
import com.example.bookshelfapp.ui.screen.SearchScreen


enum class BookShelfScreen{
    Search, Result
}


@Composable
fun BooksShelfApp(
    modifier: Modifier = Modifier
) {
    val bookshelfViewModel: BookShelfViewModel = viewModel(factory = BookShelfViewModel.factory)
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopAppBar() }
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            NavHost(
                navController = navController,
                startDestination = BookShelfScreen.Search.name
            ) {
                composable(route = BookShelfScreen.Search.name){
                    SearchScreen(
                        viewModel = bookshelfViewModel,
                        onSearch = {
                            navController.navigate(route = BookShelfScreen.Result.name)

                        },
                    )
                }

                composable(route = BookShelfScreen.Result.name){
                    val bookShelfUiState by bookshelfViewModel.bookShelfUiState.collectAsState()
                    ResultScreen(
                        bookshelfUiState = bookShelfUiState,
                        viewModel = bookshelfViewModel,
                        tryAgain = {
                            bookshelfViewModel.searchBooks()
                        },
                        homepage = {
                            bookshelfViewModel.clearUserInput()
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        modifier = modifier
    )
}