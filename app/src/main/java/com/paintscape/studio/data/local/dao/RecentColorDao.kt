package com.paintscape.studio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paintscape.studio.data.local.entity.RecentColorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentColorDao {
    @Query("SELECT * FROM recent_colors ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentColors(limit: Int = 10): Flow<List<RecentColorEntity>>

    @Query("SELECT * FROM recent_colors WHERE artworkId = :artworkId ORDER BY timestamp DESC")
    fun getRecentColorsByArtwork(artworkId: String): Flow<List<RecentColorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentColor(color: RecentColorEntity)

    @Query("DELETE FROM recent_colors WHERE id NOT IN (SELECT id FROM recent_colors ORDER BY timestamp DESC LIMIT :keepCount)")
    suspend fun trimOldColors(keepCount: Int = 50)

    @Query("DELETE FROM recent_colors")
    suspend fun clearRecentColors()
}
