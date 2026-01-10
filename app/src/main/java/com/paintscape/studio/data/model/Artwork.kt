package com.paintscape.studio.data.model

import com.paintscape.studio.data.model.SegmentationData
import com.paintscape.studio.data.model.Layer

data class Artwork(
    val id: String,
    val title: String,
    val categoryId: String,
    val previewImageRes: Int, // Placeholder for local/remote resource ID
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val colorCount: Int,
    val isPremium: Boolean = false,
    val segmentationJsonAssetPath: String // Path to raw/assets JSON file
)

enum class Difficulty(val rating: Int) {
    EASY(1),
    MEDIUM(2),
    HARD(3)
}

data class Category(
    val id: String,
    val name: String,
    val iconRes: Int // Placeholder for local icon resource ID
)



@kotlinx.serialization.Serializable
data class RegionJson(
    val number: Int,
    val paths: List<List<List<Float>>> // List of paths, where each path is a list of [x, y] points
)