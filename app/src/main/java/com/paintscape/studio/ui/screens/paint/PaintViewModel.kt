package com.paintscape.studio.ui.screens.paint

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintscape.studio.data.model.Layer
import com.paintscape.studio.data.repository.ArtworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaintViewModel @Inject constructor(
    private val artworkRepository: ArtworkRepository
) : ViewModel() {

    // StateFlow for the layers loaded from JSON
    private val _layers = MutableStateFlow<List<Layer>>(emptyList())
    val layers: StateFlow<List<Layer>> = _layers

    // Artwork dimensions for scaling
    private val _artworkWidth = MutableStateFlow(1000f)
    val artworkWidth: StateFlow<Float> = _artworkWidth

    private val _artworkHeight = MutableStateFlow(1000f)
    val artworkHeight: StateFlow<Float> = _artworkHeight

    // The "Memory": Keeps track of which regions are filled with which color
    var filledAreas by mutableStateOf(mapOf<String, Color>())
        private set

    // The "Active Brush": Keeps track of the color picked from the palette
    var selectedColor by mutableStateOf(Color.Black)
        private set

    // Load artwork by ID - looks up the artwork and loads its JSON dynamically
    fun loadArtwork(artworkId: String) {
        viewModelScope.launch {
            // Look up the artwork to get its segmentation file path
            val artwork = artworkRepository.getArtworkById(artworkId)
            if (artwork == null) {
                // Log or handle unknown artwork ID
                return@launch
            }

            // Load the segmentation data using the artwork's path
            val segmentationData = artworkRepository.getSegmentationDataForArtwork(artwork)

            segmentationData?.let {
                _layers.value = it.layers
                _artworkWidth.value = it.width.toFloat()
                _artworkHeight.value = it.height.toFloat()
            }
        }
    }

    // ACTION: When a user picks a new color from the palette
    fun onColorSelected(color: Color) {
        selectedColor = color
    }

    // ACTION: When a user taps a part of the artwork
    fun onLayerTapped(layerId: String) {
        // Adds the tapped layer and the CURRENT selected color to memory
        filledAreas = filledAreas + (layerId to selectedColor)
    }

    // ACTION: The "Eraser" that clears all filled areas
    fun clearCanvas() {
        filledAreas = emptyMap()
    }
}
