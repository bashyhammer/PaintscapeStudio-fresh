package com.paintscape.studio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.paintscape.studio.data.local.entity.ColorPaletteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ColorPaletteDao {
    @Query("SELECT * FROM color_palettes ORDER BY isDefault DESC, createdAt DESC")
    fun getAllPalettes(): Flow<List<ColorPaletteEntity>>

    @Query("SELECT * FROM color_palettes WHERE id = :paletteId")
    fun getPaletteById(paletteId: Long): Flow<ColorPaletteEntity?>

    @Query("SELECT * FROM color_palettes WHERE isDefault = 1")
    fun getDefaultPalettes(): Flow<List<ColorPaletteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPalette(palette: ColorPaletteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPalettes(palettes: List<ColorPaletteEntity>)

    @Update
    suspend fun updatePalette(palette: ColorPaletteEntity)

    @Delete
    suspend fun deletePalette(palette: ColorPaletteEntity)

    @Query("DELETE FROM color_palettes WHERE id = :paletteId")
    suspend fun deletePaletteById(paletteId: Long)
}
