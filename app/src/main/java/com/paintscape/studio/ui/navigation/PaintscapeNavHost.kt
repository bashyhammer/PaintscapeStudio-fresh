package com.paintscape.studio.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.paintscape.studio.ui.screens.gallery.GalleryScreen
import com.paintscape.studio.ui.screens.home.HomeScreen
import com.paintscape.studio.ui.screens.paint.PaintScreen
import com.paintscape.studio.ui.screens.paint.PaintViewModel
import com.paintscape.studio.ui.screens.settings.SettingsScreen

@Composable
fun PaintscapeNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home_screen"
    ) {
        // Stop 1: The Home Screen
        composable("home_screen") {
            HomeScreen(
                // When a category is clicked, go to the gallery screen
                onCategorySelected = { categoryId ->
                    navController.navigate("gallery_screen/$categoryId")
                },
                onSettingsClick = { navController.navigate("settings_screen") }
            )
        }

        // Stop 2: The Gallery Screen (shows artworks in a category)
        composable(
            route = "gallery_screen/{categoryId}",
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            GalleryScreen(
                categoryId = categoryId,
                onArtworkSelected = { artworkId ->
                    navController.navigate("paint_screen/$artworkId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Stop 3: The Paint Screen (displays the coloring canvas)
        composable(
            route = "paint_screen/{artworkId}",
            arguments = listOf(navArgument("artworkId") { type = NavType.StringType })
        ) { backStackEntry ->
            val artworkId = backStackEntry.arguments?.getString("artworkId") ?: ""
            val paintViewModel: PaintViewModel = hiltViewModel()
            PaintScreen(
                viewModel = paintViewModel,
                artworkId = artworkId,
                onBack = { navController.popBackStack() }
            )
        }

        // Stop 4: The Settings Screen
        composable("settings_screen") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}