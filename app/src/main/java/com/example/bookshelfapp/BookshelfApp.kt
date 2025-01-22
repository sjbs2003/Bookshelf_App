package com.example.bookshelfapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bookshelfapp.ui.screen.BookDetailScreen
import com.example.bookshelfapp.ui.screen.SearchScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

enum class Screens(val route: String) {
    Search("search"),
    Detail("detail/{volumeId}/{isGoogleBooks}")
}

@Composable
fun BookshelfApp(
    modifier: Modifier = Modifier
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.Search.route,
        modifier = modifier
    ) {
        composable(route = Screens.Search.route) {
            SearchScreen(
                onBookClick = { volumeId, isGoogleBooks ->
                    navController.navigate(
                        Screens.Detail.route
                            .replace("{volumeId}", volumeId)
                            .replace("{isGoogleBooks}", isGoogleBooks.toString())
                    )
                }
            )
        }

        composable(
            route = Screens.Detail.route,
            arguments = listOf(
                navArgument("volumeId") { type = NavType.StringType },
                navArgument("isGoogleBooks") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val volumeId = backStackEntry.arguments?.getString("volumeId") ?: ""
            val isGoogleBooks = backStackEntry.arguments?.getBoolean("isGoogleBooks") ?: true
            BookDetailScreen(
                viewModel = koinViewModel { parametersOf(volumeId, isGoogleBooks) },
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}