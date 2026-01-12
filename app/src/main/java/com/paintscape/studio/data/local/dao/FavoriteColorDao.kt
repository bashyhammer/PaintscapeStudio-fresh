package com.paintscape.studio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paintscape.studio.data.local.entity.FavoriteColorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteColorDao {
    @Query("SELECT * FROM favorite_colors ORDER BY usageCount DESC, lastUsed DESC")
    fun getAllFavorites(): Flow<List<FavoriteColorEntity>>

    @Query("SELECT * FROM favorite_colors WHERE colorValue = :colorValue")
    suspend fun getFavoriteByColor(colorValue: Long): FavoriteColorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteColorEntity)

    @Query("UPDATE favorite_colors SET usageCount = usageCount + 1, lastUsed = :timestamp WHERE colorValue = :colorValue")
    suspend fun incrementUsageCount(colorValue: Long, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM favorite_colors WHERE colorValue = :colorValue")
    suspend fun deleteFavorite(colorValue: Long)

    @Query("SELECT COUNT(*) FROM favorite_colors")
    suspend fun getFavoriteCount(): Int
}
