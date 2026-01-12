package com.paintscape.studio.data.model

// This is a "Data Class" - it's just a container for information
data class Category(
    val id: String,
    val name: String,
    val iconRes: Int // This will hold the image/icon for the category
)