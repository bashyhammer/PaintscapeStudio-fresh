package com.paintscape.studio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.paintscape.studio.data.local.dao.ArtworkDao
import com.paintscape.studio.data.local.entity.ArtworkProgressEntity
import com.paintscape.studio.util.RoomConverters

@Database(
    entities = [ArtworkProgressEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun artworkDao(): ArtworkDao
}