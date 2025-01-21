package com.example.bookshelfapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bookshelfapp.ui.screen.BookDetailScreen
import com.example.bookshelfapp.ui.screen.SearchScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

enum class Screens(val route: String) {
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
            SearchScreen(
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
        ) { backStackEntry ->
            val volumeId = backStackEntry.arguments?.getString("volumeId") ?: ""
            BookDetailScreen(
                viewModel = koinViewModel { parametersOf(volumeId) },
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}