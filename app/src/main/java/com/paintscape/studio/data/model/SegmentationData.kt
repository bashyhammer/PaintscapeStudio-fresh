package com.paintscape.studio.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SegmentationData(
    val name: String,
    val layers: List<Layer>
)

data class Layer(
    val label: String? = null,
    val number: Int? = null, // The Fox uses numbers instead of labels
    val points: String? = null, // For the House
    val paths: List<List<List<Float>>>? = null, // For the Fox (The triple brackets!)
    val color: String? = null,
    val colorHex: String? = null // The Fox uses colorHex
)

