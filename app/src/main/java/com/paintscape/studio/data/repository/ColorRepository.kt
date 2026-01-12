package com.paintscape.studio.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.paintscape.studio.data.local.dao.ColorPaletteDao
import com.paintscape.studio.data.local.dao.FavoriteColorDao
import com.paintscape.studio.data.local.dao.RecentColorDao
import com.paintscape.studio.data.local.entity.ColorPaletteEntity
import com.paintscape.studio.data.local.entity.FavoriteColorEntity
import com.paintscape.studio.data.local.entity.RecentColorEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ColorRepository @Inject constructor(
    private val colorPaletteDao: ColorPaletteDao,
    private val favoriteColorDao: FavoriteColorDao,
    private val recentColorDao: RecentColorDao
) {

    // --- Color Palettes ---

    fun getAllPalettes(): Flow<List<ColorPaletteEntity>> {
        return colorPaletteDao.getAllPalettes()
    }

    fun getPaletteById(paletteId: Long): Flow<ColorPaletteEntity?> {
        return colorPaletteDao.getPaletteById(paletteId)
    }

    fun getDefaultPalettes(): Flow<List<ColorPaletteEntity>> {
        return colorPaletteDao.getDefaultPalettes()
    }

    suspend fun createPalette(name: String, colors: List<Color>, isDefault: Boolean = false): Long {
        val colorLongs = colors.map { it.toArgb().toLong() }
        val palette = ColorPaletteEntity(
            name = name,
            colors = colorLongs,
            isDefault = isDefault
        )
        return colorPaletteDao.insertPalette(palette)
    }

    suspend fun insertDefaultPalettes() {
        val defaultPalettes = listOf(
            ColorPaletteEntity(
                name = "Basic Colors",
                colors = listOf(
                    Color.Red.toArgb().toLong(),
                    Color.Blue.toArgb().toLong(),
                    Color.Green.toArgb().toLong(),
                    Color.Yellow.toArgb().toLong(),
                    Color(0xFFFFA500).toArgb().toLong(), // Orange
                    Color.Magenta.toArgb().toLong(),
                    Color.Black.toArgb().toLong(),
                    Color.White.toArgb().toLong()
                ),
                isDefault = true
            ),
            ColorPaletteEntity(
                name = "Pastels",
                colors = listOf(
                    Color(0xFFFFB3BA).toArgb().toLong(), // Pastel Pink
                    Color(0xFFFFDFBA).toArgb().toLong(), // Pastel Peach
                    Color(0xFFFFFFBA).toArgb().toLong(), // Pastel Yellow
                    Color(0xFFBAFFC9).toArgb().toLong(), // Pastel Mint
                    Color(0xFFBAE1FF).toArgb().toLong(), // Pastel Blue
                    Color(0xFFD4BAFF).toArgb().toLong(), // Pastel Lavender
                    Color(0xFFFFBAE8).toArgb().toLong(), // Pastel Rose
                    Color(0xFFF0F0F0).toArgb().toLong()  // Light Gray
                ),
                isDefault = true
            ),
            ColorPaletteEntity(
                name = "Earth Tones",
                colors = listOf(
                    Color(0xFF8B4513).toArgb().toLong(), // Saddle Brown
                    Color(0xFFD2691E).toArgb().toLong(), // Chocolate
                    Color(0xFFCD853F).toArgb().toLong(), // Peru
                    Color(0xFFDEB887).toArgb().toLong(), // Burlywood
                    Color(0xFF9ACD32).toArgb().toLong(), // Yellow Green
                    Color(0xFF556B2F).toArgb().toLong(), // Dark Olive Green
                    Color(0xFF8FBC8F).toArgb().toLong(), // Dark Sea Green
                    Color(0xFF2F4F4F).toArgb().toLong()  // Dark Slate Gray
                ),
                isDefault = true
            ),
            ColorPaletteEntity(
                name = "Ocean",
                colors = listOf(
                    Color(0xFF000080).toArgb().toLong(), // Navy
                    Color(0xFF0000CD).toArgb().toLong(), // Medium Blue
                    Color(0xFF1E90FF).toArgb().toLong(), // Dodger Blue
                    Color(0xFF00BFFF).toArgb().toLong(), // Deep Sky Blue
                    Color(0xFF87CEEB).toArgb().toLong(), // Sky Blue
                    Color(0xFF40E0D0).toArgb().toLong(), // Turquoise
                    Color(0xFF00CED1).toArgb().toLong(), // Dark Turquoise
                    Color(0xFF20B2AA).toArgb().toLong()  // Light Sea Green
                ),
                isDefault = true
            ),
            ColorPaletteEntity(
                name = "Vibrant",
                colors = listOf(
                    Color(0xFFFF1493).toArgb().toLong(), // Deep Pink
                    Color(0xFFFF4500).toArgb().toLong(), // Orange Red
                    Color(0xFFFFD700).toArgb().toLong(), // Gold
                    Color(0xFF7FFF00).toArgb().toLong(), // Chartreuse
                    Color(0xFF00FF7F).toArgb().toLong(), // Spring Green
                    Color(0xFF00FFFF).toArgb().toLong(), // Cyan
                    Color(0xFF8A2BE2).toArgb().toLong(), // Blue Violet
                    Color(0xFFFF00FF).toArgb().toLong()  // Magenta
                ),
                isDefault = true
            )
        )
        colorPaletteDao.insertPalettes(defaultPalettes)
    }

    suspend fun updatePalette(palette: ColorPaletteEntity) {
        colorPaletteDao.updatePalette(palette)
    }

    suspend fun deletePalette(paletteId: Long) {
        colorPaletteDao.deletePaletteById(paletteId)
    }

    // --- Favorite Colors ---

    fun getAllFavorites(): Flow<List<Color>> {
        return favoriteColorDao.getAllFavorites().map { favorites ->
            favorites.map { Color(it.colorValue.toInt()) }
        }
    }

    suspend fun addToFavorites(color: Color) {
        val colorValue = color.toArgb().toLong()
        val existing = favoriteColorDao.getFavoriteByColor(colorValue)

        if (existing != null) {
            favoriteColorDao.incrementUsageCount(colorValue)
        } else {
            favoriteColorDao.insertFavorite(
                FavoriteColorEntity(
                    colorValue = colorValue,
                    usageCount = 1
                )
            )
        }
    }

    suspend fun removeFromFavorites(color: Color) {
        favoriteColorDao.deleteFavorite(color.toArgb().toLong())
    }

    suspend fun getFavoriteCount(): Int {
        return favoriteColorDao.getFavoriteCount()
    }

    // --- Recent Colors ---

    fun getRecentColors(limit: Int = 10): Flow<List<Color>> {
        return recentColorDao.getRecentColors(limit).map { recent ->
            recent.map { Color(it.colorValue.toInt()) }
        }
    }

    fun getRecentColorsByArtwork(artworkId: String): Flow<List<Color>> {
        return recentColorDao.getRecentColorsByArtwork(artworkId).map { recent ->
            recent.map { Color(it.colorValue.toInt()) }
        }
    }

    suspend fun addRecentColor(color: Color, artworkId: String? = null) {
        recentColorDao.insertRecentColor(
            RecentColorEntity(
                colorValue = color.toArgb().toLong(),
                artworkId = artworkId
            )
        )
        // Trim old colors to keep database size manageable
        recentColorDao.trimOldColors(50)
    }

    suspend fun clearRecentColors() {
        recentColorDao.clearRecentColors()
    }

    // --- Helper Functions ---

    fun convertLongListToColors(colorLongs: List<Long>): List<Color> {
        return colorLongs.map { Color(it.toInt()) }
    }

    fun convertColorsToLongList(colors: List<Color>): List<Long> {
        return colors.map { it.toArgb().toLong() }
    }
}
