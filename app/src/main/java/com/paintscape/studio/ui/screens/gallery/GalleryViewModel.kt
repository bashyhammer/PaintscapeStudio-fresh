package com.paintscape.studio.ui.screens.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintscape.studio.data.model.Artwork
import com.paintscape.studio.data.repository.ArtworkRepository
import com.paintscape.studio.data.repository.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class GalleryUiState(
    val categoryId: String = "",
    val categoryTitle: String = "Loading...",
    val artworks: List<Artwork> = emptyList(),
    val isPremiumUser: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class GalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    artworkRepository: ArtworkRepository,
    billingRepository: BillingRepository
) : ViewModel() {

    private val categoryId: String = savedStateHandle.get<String>("categoryId") ?: "animals"

    private val allArtworks = artworkRepository.getArtworkByCategory(categoryId)
    private val category = artworkRepository.getCategories().firstOrNull { it.id == categoryId }

    val state: StateFlow<GalleryUiState> = combine(
        billingRepository.premiumStatus,
        billingRepository.purchasedArtPacks
    ) { isPremiumUser, purchasedPacks ->
        // In a real app, purchasedPacks would be used to unlock IAP packs

        GalleryUiState(
            categoryId = categoryId,
            categoryTitle = category?.name ?: "Artwork Gallery",
            artworks = allArtworks,
            isPremiumUser = isPremiumUser,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GalleryUiState(categoryId = categoryId, categoryTitle = category?.name ?: "Artwork Gallery")
    )
}