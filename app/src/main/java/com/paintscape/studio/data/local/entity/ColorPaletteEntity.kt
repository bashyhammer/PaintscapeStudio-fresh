package com.paintscape.studio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "color_palettes")
data class ColorPaletteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colors: List<Long>, // Store colors as Long (ARGB values)
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
