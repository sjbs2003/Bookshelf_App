package com.example.bookshelfapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bookshelfapp.ui.screen.BookDetailScreen
import com.example.bookshelfapp.ui.screen.SearchScreen
import com.example.bookshelfapp.viewModel.AppViewModelProvider
import com.example.bookshelfapp.viewModel.DetailViewModel
import com.example.bookshelfapp.viewModel.SearchViewModel


enum class Screens(val route : String) {
    Search("search"),
    Detail("detail/{volumeId}")
}

@Composable
fun BookshelfApp(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
    navController = navController,
    startDestination = Screens.Search.route,
    modifier = modifier
    ) {
        composable(route = Screens.Search.route) {
            val searchViewModel: SearchViewModel = viewModel(
                factory = AppViewModelProvider.Factory
            )
            SearchScreen(
                viewModel = searchViewModel,
                onBookClick = { volumeId ->
                    navController.navigate(
                        Screens.Detail.route.replace("{volumeId}", volumeId)
                    )
                }
            )
        }

        composable(
            route = Screens.Detail.route,
            arguments = listOf(
                navArgument("volumeId") { type = NavType.StringType }
            )
        ) {
            val detailViewModel: DetailViewModel = viewModel(
                factory = AppViewModelProvider.Factory
            )
            BookDetailScreen(
                viewModel = detailViewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}