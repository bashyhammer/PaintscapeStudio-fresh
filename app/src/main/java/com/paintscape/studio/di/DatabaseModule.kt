package com.paintscape.studio.di

import android.content.Context
import androidx.room.Room
import com.paintscape.studio.data.local.AppDatabase
import com.paintscape.studio.data.local.dao.ArtworkDao
import com.paintscape.studio.data.local.dao.ColorPaletteDao
import com.paintscape.studio.data.local.dao.FavoriteColorDao
import com.paintscape.studio.data.local.dao.RecentColorDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "paintscape-db"
        )
            .fallbackToDestructiveMigration() // For development: wipe DB on schema changes
            .build()
    }

    @Provides
    fun provideArtworkDao(appDatabase: AppDatabase): ArtworkDao {
        return appDatabase.artworkDao()
    }

    @Provides
    fun provideColorPaletteDao(appDatabase: AppDatabase): ColorPaletteDao {
        return appDatabase.colorPaletteDao()
    }

    @Provides
    fun provideFavoriteColorDao(appDatabase: AppDatabase): FavoriteColorDao {
        return appDatabase.favoriteColorDao()
    }

    @Provides
    fun provideRecentColorDao(appDatabase: AppDatabase): RecentColorDao {
        return appDatabase.recentColorDao()
    }
}