package com.paintscape.studio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_colors")
data class RecentColorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val colorValue: Long, // Store color as Long (ARGB value)
    val timestamp: Long = System.currentTimeMillis(),
    val artworkId: String? = null // Optional: track which artwork used this color
)
