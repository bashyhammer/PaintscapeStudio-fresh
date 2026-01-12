package com.paintscape.studio.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SegmentationData(
    val layers: List<Layer>,                      // Required - the actual artwork data
    val width: Int = 1000,                        // Required with default - artwork width in pixels
    val height: Int = 1000,                       // Required with default - artwork height in pixels
    val artworkId: String? = null,                // Optional - identifier for the artwork
    val name: String? = null,                     // Optional - display name
    val palette: Map<String, String>? = null      // Optional - color palette mapping
)

@Serializable
data class Layer(
    val number: Int? = null,                      // Layer identifier (Fox style)
    val label: String? = null,                    // Layer identifier (House style)
    val paths: List<List<List<Float>>>? = null,   // Nested coordinates (Fox style)
    val points: String? = null,                   // String coordinates (House style)
    val color: String? = null,                    // Fill color
    val colorHex: String? = null                  // Alternate color format
) {
    // Helper to get a consistent layer ID regardless of format
    val id: String get() = number?.toString() ?: label ?: ""
}

