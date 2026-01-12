package com.paintscape.studio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_colors")
data class FavoriteColorEntity(
    @PrimaryKey
    val colorValue: Long, // Store color as Long (ARGB value)
    val usageCount: Int = 1,
    val lastUsed: Long = System.currentTimeMillis()
)
