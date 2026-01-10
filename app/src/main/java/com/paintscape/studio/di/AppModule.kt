package com.paintscape.studio.di

import android.content.Context
import com.paintscape.studio.data.local.dao.ArtworkDao // Updated to match your path
import com.paintscape.studio.data.local.database.PaintscapeDatabase // Ensure this path is correct
import com.paintscape.studio.data.repository.ArtworkRepository // Matches your file
import com.paintscape.studio.util.SegmentationEngine // The missing piece!
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PaintscapeDatabase {
        return PaintscapeDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideArtworkRepository(
        dao: ArtworkDao,
        engine: SegmentationEngine
    ): ArtworkRepository {
        return ArtworkRepository(dao, engine)
    }
}