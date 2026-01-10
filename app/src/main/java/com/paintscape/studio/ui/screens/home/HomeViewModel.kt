package com.paintscape.studio.ui.screens.home

import androidx.lifecycle.ViewModel
import com.paintscape.studio.data.model.Category
import com.paintscape.studio.data.repository.ArtworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val artworkRepository: ArtworkRepository
) : ViewModel() {

    val categories: List<Category> = artworkRepository.getCategories()
}