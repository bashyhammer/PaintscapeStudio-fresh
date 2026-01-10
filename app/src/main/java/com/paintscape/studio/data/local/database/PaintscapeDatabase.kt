package com.paintscape.studio.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.paintscape.studio.data.local.dao.ArtworkDao
import com.paintscape.studio.data.local.entity.ArtworkProgressEntity
import com.paintscape.studio.util.RoomConverters // This is the translator we will build next

@Database(entities = [ArtworkProgressEntity::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class) // This tells the database to use the translator
abstract class PaintscapeDatabase : RoomDatabase() {

    abstract fun artworkDao(): ArtworkDao

    companion object {
        @Volatile
        private var INSTANCE: PaintscapeDatabase? = null

        fun getInstance(context: Context): PaintscapeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PaintscapeDatabase::class.java,
                    "paintscape_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}