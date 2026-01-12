package com.paintscape.studio.ui.screens.paint

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintscape.studio.data.local.entity.ColorPaletteEntity
import com.paintscape.studio.data.model.Layer
import com.paintscape.studio.data.repository.ArtworkRepository
import com.paintscape.studio.data.repository.ColorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaintViewModel @Inject constructor(
    private val artworkRepository: ArtworkRepository,
    private val colorRepository: ColorRepository
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

    // Color Palette Management
    val allPalettes: StateFlow<List<ColorPaletteEntity>> =
        colorRepository.getAllPalettes()
            .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentPalette = MutableStateFlow<ColorPaletteEntity?>(null)
    val currentPalette: StateFlow<ColorPaletteEntity?> = _currentPalette

    val favoriteColors: StateFlow<List<Color>> =
        colorRepository.getAllFavorites()
            .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    val recentColors: StateFlow<List<Color>> =
        colorRepository.getRecentColors(10)
            .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    // Current artwork ID for tracking recent colors
    private var currentArtworkId: String? = null

    // Load artwork by ID - looks up the artwork and loads its JSON dynamically
    fun loadArtwork(artworkId: String) {
        viewModelScope.launch {
            // Store artwork ID for tracking recent colors
            currentArtworkId = artworkId

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

            // Load first default palette if no palette selected
            if (_currentPalette.value == null) {
                val defaultPalettes = colorRepository.getDefaultPalettes()
                defaultPalettes.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
                    .value.firstOrNull()?.let { palette ->
                        _currentPalette.value = palette
                    }
            }
        }
    }

    // ACTION: When a user picks a new color from the palette
    fun onColorSelected(color: Color) {
        selectedColor = color
        // Track color usage
        viewModelScope.launch {
            colorRepository.addRecentColor(color, currentArtworkId)
        }
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

    // --- Color Palette Management Functions ---

    fun selectPalette(palette: ColorPaletteEntity) {
        _currentPalette.value = palette
    }

    fun toggleFavoriteColor(color: Color) {
        viewModelScope.launch {
            val favorites = favoriteColors.value
            if (favorites.contains(color)) {
                colorRepository.removeFromFavorites(color)
            } else {
                colorRepository.addToFavorites(color)
            }
        }
    }

    fun createCustomPalette(name: String, colors: List<Color>) {
        viewModelScope.launch {
            val paletteId = colorRepository.createPalette(name, colors)
            // Optionally select the newly created palette
            colorRepository.getPaletteById(paletteId)
                .stateIn(viewModelScope, SharingStarted.Eagerly, null)
                .value?.let { palette ->
                    _currentPalette.value = palette
                }
        }
    }

    fun deletePalette(paletteId: Long) {
        viewModelScope.launch {
            colorRepository.deletePalette(paletteId)
            // If current palette was deleted, switch to first default palette
            if (_currentPalette.value?.id == paletteId) {
                val defaultPalettes = colorRepository.getDefaultPalettes()
                defaultPalettes.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
                    .value.firstOrNull()?.let { palette ->
                        _currentPalette.value = palette
                    }
            }
        }
    }
}
