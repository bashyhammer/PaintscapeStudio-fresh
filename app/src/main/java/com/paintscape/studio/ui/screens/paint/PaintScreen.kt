package com.paintscape.studio.ui.screens.paint

import android.graphics.RectF
import android.graphics.Region
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.paintscape.studio.data.model.Layer
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaintScreen(
    viewModel: PaintViewModel,
    artworkId: String,
    onBack: () -> Unit = {}
) {
    // Load the artwork when the screen first appears
    LaunchedEffect(artworkId) {
        viewModel.loadArtwork(artworkId)
    }

    // Observe state from ViewModel
    val layers by viewModel.layers.collectAsState()
    val artworkWidth by viewModel.artworkWidth.collectAsState()
    val artworkHeight by viewModel.artworkHeight.collectAsState()

    val paletteColors = listOf(
        Color.Red, Color.Blue, Color.Green, Color.Yellow,
        Color(0xFFFFA500), Color.Magenta, Color.Black
    )

    // Zoom and Pan state
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    // Handles pinch-to-zoom and pan gestures
    val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
        // Update zoom (clamp between 1x and 5x)
        val newZoom = (zoomScale * zoomChange).coerceIn(1f, 5f)
        zoomScale = newZoom

        // Update pan offset (only allow panning when zoomed in)
        if (newZoom > 1f) {
            // Calculate max pan based on how much extra space the zoomed artwork takes
            // When zoomed 2x, you can pan half the artwork size in each direction
            val maxPanX = (artworkWidth * (newZoom - 1f)) / 2f
            val maxPanY = (artworkHeight * (newZoom - 1f)) / 2f

            val newOffset = panOffset + offsetChange
            panOffset = Offset(
                x = newOffset.x.coerceIn(-maxPanX, maxPanX),
                y = newOffset.y.coerceIn(-maxPanY, maxPanY)
            )
        } else {
            panOffset = Offset.Zero // Reset pan when fully zoomed out
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paint") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Reset zoom button (only show when zoomed in)
                    if (zoomScale > 1f) {
                        TextButton(onClick = {
                            zoomScale = 1f
                            panOffset = Offset.Zero
                        }) {
                            Text("Reset")
                        }
                    }
                    TextButton(onClick = { viewModel.clearCanvas() }) {
                        Text("Clear")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // The Coloring Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clipToBounds() // Prevents zoomed content from overflowing
                    .transformable(state = transformableState), // Handle pinch-to-zoom and pan
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(layers, artworkWidth, artworkHeight, zoomScale, panOffset) {
                            detectTapGestures { offset ->
                                // Guard: Don't process taps if artwork dimensions aren't loaded yet
                                if (artworkWidth <= 0f || artworkHeight <= 0f) return@detectTapGestures

                                val canvasWidth = size.width.toFloat()
                                val canvasHeight = size.height.toFloat()

                                // Base scale to fit artwork in canvas
                                val baseScale = min(
                                    canvasWidth / artworkWidth,
                                    canvasHeight / artworkHeight
                                )

                                // Combined scale (base fit + user zoom)
                                val totalScale = baseScale * zoomScale

                                // Center of canvas
                                val centerX = canvasWidth / 2
                                val centerY = canvasHeight / 2

                                // Where the artwork center is drawn (with pan offset)
                                val artworkCenterX = centerX + panOffset.x
                                val artworkCenterY = centerY + panOffset.y

                                // Convert tap to artwork coordinates
                                // 1. Offset from the artwork center
                                // 2. Divide by total scale to get artwork coordinates
                                // 3. Add half artwork size (since artwork origin is top-left)
                                val artworkX = (offset.x - artworkCenterX) / totalScale + artworkWidth / 2
                                val artworkY = (offset.y - artworkCenterY) / totalScale + artworkHeight / 2

                                // Check layers in reverse order (top layers first)
                                for (layer in layers.reversed()) {
                                    if (isPointInLayer(layer, artworkX, artworkY)) {
                                        val layerId = layer.id
                                        viewModel.onLayerTapped(layerId)
                                        break
                                    }
                                }
                            }
                        }
                ) {
                    // Guard: Don't draw if artwork dimensions aren't loaded yet
                    if (artworkWidth <= 0f || artworkHeight <= 0f) return@Canvas

                    // Base scale to fit artwork in canvas
                    val baseScale = min(
                        size.width / artworkWidth,
                        size.height / artworkHeight
                    )

                    // Combined scale (base fit + user zoom)
                    val totalScale = baseScale * zoomScale

                    // Center of canvas
                    val centerX = size.width / 2
                    val centerY = size.height / 2

                    // Apply transformation: center artwork, then apply zoom and pan
                    withTransform({
                        // Move to center of canvas + pan offset
                        translate(centerX + panOffset.x, centerY + panOffset.y)
                        // Apply combined scale
                        scale(totalScale, totalScale, pivot = Offset.Zero)
                        // Offset so artwork center is at origin
                        translate(-artworkWidth / 2, -artworkHeight / 2)
                    }) {
                        // Draw each layer
                        layers.forEach { layer ->
                            val path = Path().apply {
                                layer.paths?.forEach { shape ->
                                    shape.forEachIndexed { index, point ->
                                        val x = point[0]
                                        val y = point[1]
                                        if (index == 0) moveTo(x, y) else lineTo(x, y)
                                    }
                                    close()
                                }
                            }

                            // Draw the filled color (defaults to White if not painted)
                            val layerId = layer.id
                            drawPath(
                                path = path,
                                color = viewModel.filledAreas[layerId] ?: Color.White
                            )

                            // Draw the black outline
                            drawPath(
                                path = path,
                                color = Color.Black,
                                style = Stroke(width = 2f / totalScale) // Adjust stroke for scale
                            )
                        }
                    }
                }
            }

            // Color Palette
            Text(text = "Pick a Color", modifier = Modifier.padding(top = 8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                paletteColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { viewModel.onColorSelected(color) }
                            .border(
                                width = if (viewModel.selectedColor == color) 4.dp else 1.dp,
                                color = if (viewModel.selectedColor == color) Color.Gray else Color.LightGray,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

// Helper function to check if a point is inside a layer's path
private fun isPointInLayer(layer: Layer, x: Float, y: Float): Boolean {
    layer.paths?.forEach { shape ->
        val path = android.graphics.Path()
        shape.forEachIndexed { index, point ->
            val px = point[0]
            val py = point[1]
            if (index == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        path.close()

        // Use Region to check if point is inside the path
        val bounds = RectF()
        path.computeBounds(bounds, true)

        // Expand bounds slightly to avoid edge cases with small regions
        val region = Region()
        region.setPath(path, Region(
            kotlin.math.floor(bounds.left).toInt(),
            kotlin.math.floor(bounds.top).toInt(),
            kotlin.math.ceil(bounds.right).toInt(),
            kotlin.math.ceil(bounds.bottom).toInt()
        ))

        // Use rounding instead of truncation for better precision
        if (region.contains(kotlin.math.round(x).toInt(), kotlin.math.round(y).toInt())) {
            return true
        }
    }
    return false
}
