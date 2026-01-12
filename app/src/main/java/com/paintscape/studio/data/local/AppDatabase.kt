package com.paintscape.studio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.paintscape.studio.data.local.dao.ArtworkDao
import com.paintscape.studio.data.local.dao.ColorPaletteDao
import com.paintscape.studio.data.local.dao.FavoriteColorDao
import com.paintscape.studio.data.local.dao.RecentColorDao
import com.paintscape.studio.data.local.entity.ArtworkProgressEntity
import com.paintscape.studio.data.local.entity.ColorPaletteEntity
import com.paintscape.studio.data.local.entity.FavoriteColorEntity
import com.paintscape.studio.data.local.entity.RecentColorEntity
import com.paintscape.studio.util.RoomConverters

@Database(
    entities = [
        ArtworkProgressEntity::class,
        ColorPaletteEntity::class,
        FavoriteColorEntity::class,
        RecentColorEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun artworkDao(): ArtworkDao
    abstract fun colorPaletteDao(): ColorPaletteDao
    abstract fun favoriteColorDao(): FavoriteColorDao
    abstract fun recentColorDao(): RecentColorDao
}