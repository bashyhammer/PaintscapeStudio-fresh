package com.paintscape.studio.data.repository

import com.paintscape.studio.R
import com.paintscape.studio.data.local.dao.ArtworkDao
import com.paintscape.studio.data.local.entity.ArtworkProgressEntity
import com.paintscape.studio.data.model.Artwork
import com.paintscape.studio.data.model.Category
import com.paintscape.studio.data.model.Difficulty
import com.paintscape.studio.data.model.SegmentationData
import com.paintscape.studio.util.SegmentationEngine
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtworkRepository @Inject constructor(
    private val artworkDao: ArtworkDao,
    private val segmentationEngine: SegmentationEngine
) {
    // --- Mock Data ---
    private val mockCategories = listOf(
        Category("animals", "Animals", R.drawable.ic_placeholder_category),
        Category("gardens", "Gardens", R.drawable.ic_placeholder_category),
        Category("fantasy", "Fantasy Scenes", R.drawable.ic_placeholder_category),
        Category("mandalas", "Mandalas", R.drawable.ic_placeholder_category),
        Category("crystals", "Crystals / Moonstones", R.drawable.ic_placeholder_category)
    )

    private val mockArtwork = listOf(
        // We link "fox.json" here
        Artwork("art_001", "Mystic Fox", "animals", R.drawable.ic_placeholder_category, Difficulty.MEDIUM, 8, false, "fox.json"),
        Artwork("art_002", "Crystal Garden", "crystals", R.drawable.ic_placeholder_category, Difficulty.HARD, 12, true, "garden_seg.json"),
        Artwork("art_003", "Zenith Bloom", "mandalas", R.drawable.ic_placeholder_category, Difficulty.EASY, 6, false, "mandala_seg.json"),
        Artwork("art_004", "Starlight Spire", "fantasy", R.drawable.ic_placeholder_category, Difficulty.HARD, 15, true, "spire_seg.json")
    )
    // --- End Mock Data ---

    fun getCategories(): List<Category> = mockCategories
    fun getArtworkByCategory(categoryId: String): List<Artwork> {
        return if (categoryId == "user_photos") emptyList() // User photo logic separate
        else mockArtwork.filter { it.categoryId == categoryId }
    }

    fun getAnimalData(animalName: String): SegmentationData? {
        // 2. Point it to the new loadAnimalData function we wrote
        return segmentationEngine.loadAnimalData(animalName)
    }

    fun getArtworkProgress(artworkId: String): Flow<ArtworkProgressEntity?> {
        return artworkDao.getProgressById(artworkId)
    }

    suspend fun saveProgress(progress: ArtworkProgressEntity) {
        artworkDao.insertOrUpdateProgress(progress)
    }
}