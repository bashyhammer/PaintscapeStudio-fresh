package com.paintscape.studio.ui.screens.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.paintscape.studio.data.model.Artwork
import androidx.compose.foundation.background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    @Suppress("UNUSED_PARAMETER") categoryId: String, // Will be used for category-specific features
    viewModel: GalleryViewModel = hiltViewModel(),
    onArtworkSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    // Collect the UI state
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.categoryTitle, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(padding)
            ) {
                if (state.artworks.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Text(
                            text = "No artwork found in this category yet. Dreaming up new designs...",
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    items(state.artworks) { artwork ->
                        ArtworkCard(
                            artwork = artwork,
                            isPremiumUser = state.isPremiumUser,
                            onArtworkClick = {
                                // Only proceed if the user is premium or the artwork is free
                                if (artwork.isPremium && !state.isPremiumUser) {
                                    // In a real app, this would navigate to the Paywall
                                    // For simplicity here, we'll log/show a local message.
                                    println("Artwork is premium, redirect to paywall!")
                                } else {
                                    onArtworkSelected(artwork.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArtworkCard(
    artwork: Artwork,
    isPremiumUser: Boolean,
    onArtworkClick: () -> Unit
) {
    val isLocked = artwork.isPremium && !isPremiumUser

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onArtworkClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Placeholder Image (Coil is assumed to be available)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://placehold.co/400x400/3A4F6A/FFFFFF?text=${artwork.title.replace(" ", "+")}")
                    .crossfade(true)
                    .build(),
                contentDescription = artwork.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Artwork Metadata Overlay (Bottom)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp)
            ) {
                Text(
                    text = artwork.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Colors: ${artwork.colorCount}",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Difficulty: ${artwork.difficulty.name}",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Premium Lock Overlay
            if (isLocked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = "Premium Artwork Locked",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}