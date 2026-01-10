package com.paintscape.studio.ui.screens.paint

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintscape.studio.data.model.SegmentationData
import com.paintscape.studio.util.SegmentationEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaintViewModel @Inject constructor(
    private val segmentationEngine: SegmentationEngine
) : ViewModel() {

    // 1. Holds the original map data (the lines/shapes)
    private val _segmentationData = mutableStateOf<SegmentationData?>(null)
    val segmentationData: State<SegmentationData?> = _segmentationData

    // 2. The Sticky-Note Pad: Remembers which layer index has which color
    // Key = Layer Number, Value = The Color you tapped
    val paintedColors = mutableStateMapOf<Int, Color>()

    init {
        loadAnimal("art_001")
    }

    private fun loadAnimal(name: String) {
        viewModelScope.launch {
            val data = segmentationEngine.loadAnimalData(name)
            _segmentationData.value = data
        }
    }

    // 3. The Action: This is called when you tap a shape
    fun onLayerTapped(layerIndex: Int, color: Color) {
        paintedColors[layerIndex] = color
    }
    // This clears the map, which tells Compose to redraw everything
// back to the original JSON colors!
    fun clearAllColors() {
        paintedColors.clear()
    }
}