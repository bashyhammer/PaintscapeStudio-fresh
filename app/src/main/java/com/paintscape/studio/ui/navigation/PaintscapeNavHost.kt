package com.paintscape.studio.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.paintscape.studio.ui.screens.gallery.GalleryScreen
import com.paintscape.studio.ui.screens.home.HomeScreen
import com.paintscape.studio.ui.screens.paint.PaintScreen
import com.paintscape.studio.ui.screens.paywall.PaywallScreen
import com.paintscape.studio.ui.screens.settings.SettingsScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.paintscape.studio.ui.screens.paint.PaintViewModel
import com.paintscape.studio.data.model.SegmentationData
import androidx.compose.runtime.getValue // For the 'by' keyword

@Composable
fun PaintscapeNavHost(
    navController: NavHostController,
    startDestination: String = "home"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 1. Home Screen (Lines 25-26)
        composable("home") {
            // Check your HomeScreen.kt - it might use different names.
            // We'll use the most likely ones based on your errors.
            HomeScreen(
                onCategorySelected = { categoryId ->
                    navController.navigate("gallery/$categoryId")
                },
                onSettingsClick = { navController.navigate("settings") }
            )
        }

        // 2. Gallery Screen (Lines 33-34)
        composable("gallery/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: "all"
            GalleryScreen(
                categoryId = categoryId, // Pass the ID it's asking for
                onBack = { navController.popBackStack() }, // Fixed name
                onArtworkSelected = { artworkId ->
                    navController.navigate("paint/$artworkId")
                }
            )
        }

        // 3. Paint Screen
        composable("paint/{artworkId}") { backStackEntry ->
            val artworkId = backStackEntry.arguments?.getString("artworkId") ?: ""

            // 1. This line creates the ViewModel (The Plate)
            val paintViewModel: PaintViewModel = hiltViewModel()

            // 2. This line gets the data (The Meal)
            val data by paintViewModel.segmentationData

            PaintScreen(
                artworkId = artworkId,
                onFinish = { navController.popBackStack() },
                segmentationData = data,
                // 3. HAND THE PLATE TO THE SCREEN
                viewModel = paintViewModel
            )
        }

        // 4. Settings Screen
        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // 5. Paywall Screen
        composable("paywall") {
            PaywallScreen(
                onBackClick = { navController.popBackStack() },
                onPurchaseClick = { _ ->
                    navController.popBackStack()
                }
            )
        }
    }
}