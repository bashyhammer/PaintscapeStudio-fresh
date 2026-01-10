package com.paintscape.studio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paintscape.studio.data.local.entity.ArtworkProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtworkDao {
    @Query("SELECT * FROM artwork_progress WHERE artworkId = :artworkId")
    fun getProgressById(artworkId: String): Flow<ArtworkProgressEntity?>

    @Query("SELECT * FROM artwork_progress")
    fun getAllProgress(): Flow<List<ArtworkProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: ArtworkProgressEntity)
}