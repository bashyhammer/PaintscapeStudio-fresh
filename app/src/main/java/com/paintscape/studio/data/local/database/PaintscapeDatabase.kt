package com.paintscape.studio.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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
import com.paintscape.studio.util.RoomConverters // This is the translator we will build next

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
@TypeConverters(RoomConverters::class) // This tells the database to use the translator
abstract class PaintscapeDatabase : RoomDatabase() {

    abstract fun artworkDao(): ArtworkDao
    abstract fun colorPaletteDao(): ColorPaletteDao
    abstract fun favoriteColorDao(): FavoriteColorDao
    abstract fun recentColorDao(): RecentColorDao

    companion object {
        @Volatile
        private var INSTANCE: PaintscapeDatabase? = null

        fun getInstance(context: Context): PaintscapeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PaintscapeDatabase::class.java,
                    "paintscape_database"
                )
                    .fallbackToDestructiveMigration() // For development: wipe DB on schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}