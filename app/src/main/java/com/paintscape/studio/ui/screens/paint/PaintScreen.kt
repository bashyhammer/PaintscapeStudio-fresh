package com.paintscape.studio.ui.screens.paint

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.paintscape.studio.data.model.Layer
import com.paintscape.studio.data.model.SegmentationData
import androidx.core.graphics.toColorInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaintScreen(
    artworkId: String,
    onFinish: () -> Unit,
    segmentationData: SegmentationData? = null,
    viewModel: PaintViewModel // This connects the screen to the "Brain"
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Painting: $artworkId") },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                // ADD THIS SECTION BELOW:
                actions = {
                    TextButton(onClick = { viewModel.clearAllColors() }) {
                        Text("Clear All", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(segmentationData) {
                        detectTapGestures { offset ->
                            segmentationData?.layers?.forEachIndexed { index, layer ->
                                val path = createPathForLayer(layer)
                                if (path.getBounds().contains(offset)) {
                                    viewModel.onLayerTapped(index, Color.Green)
                                }
                            }
                        }
                    }
            ) {
                // 1. Safety check: only draw if data exists
                val layers = segmentationData?.layers ?: return@Canvas

                layers.forEachIndexed { index, layer ->
                    // 2. Create the shape
                    val path = createPathForLayer(layer)

                    // 3. COLOR LOGIC (The "Flat" way)
                    // We get the string directly from the layer
                    val layerHex = layer.color ?: layer.colorHex ?: "#D3D3D3"

                    // We turn it into a color directly
                    val defaultColor = try {
                        Color(layerHex.toColorInt())
                    } catch (e: Exception) {
                        println("Color Error: ${e.message}") // Now 'e' is used, so the warning vanishes!
                        Color.LightGray
                    }

                    // We check the ViewModel for a painted color
                    val finalColor = viewModel.paintedColors[index] ?: defaultColor

                    // 4. DRAW
                    drawPath(path = path, color = finalColor, style = Fill)
                    drawPath(path = path, color = Color.Black, style = Stroke(width = 2f))
                }
            }
            }
        }
}

/**
 * HELPER FUNCTION: This builds the shape for us so we don't have to
 * write the point/path logic twice.
 */
fun createPathForLayer(layer: Layer): Path {
    val path = Path()
    // STYLE A: The House (String format)
    layer.points?.let { pts ->
        val pointGroups = pts.split(" ")
        pointGroups.forEachIndexed { index, pStr ->
            val coords = pStr.split(",")
            if (coords.size == 2) {
                val x = coords[0].toFloatOrNull() ?: 0f
                val y = coords[1].toFloatOrNull() ?: 0f
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
        }
    }

    // STYLE B: The Fox (Nested Lists format)
    layer.paths?.forEach { shape ->
        shape.forEachIndexed { index, point ->
            if (point.size >= 2) {
                val x = point[0]
                val y = point[1]
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
        }
    }
    path.close()
    return path
}