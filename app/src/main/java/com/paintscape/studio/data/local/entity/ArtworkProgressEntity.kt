package com.paintscape.studio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artwork_progress")
data class ArtworkProgressEntity(
    @PrimaryKey
    val artworkId: String,
    val totalRegions: Int,
    val paintedRegions: Set<Int>, // Set of region numbers that are completed
    val progressPercentage: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)