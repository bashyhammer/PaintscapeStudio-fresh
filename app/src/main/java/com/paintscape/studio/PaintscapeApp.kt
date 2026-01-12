package com.paintscape.studio

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.paintscape.studio.data.repository.ColorRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class PaintscapeApp : Application() {

    @Inject
    lateinit var colorRepository: ColorRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        // Initialize AdMob SDK
        MobileAds.initialize(this) {}

        // Initialize default color palettes on first app launch
        applicationScope.launch {
            initializeDefaultPalettes()
        }
    }

    private suspend fun initializeDefaultPalettes() {
        // Check if default palettes already exist
        val allPalettes = mutableListOf<com.paintscape.studio.data.local.entity.ColorPaletteEntity>()
        colorRepository.getAllPalettes().collect { palettes ->
            allPalettes.addAll(palettes)
        }

        // Only insert default palettes if none exist
        if (allPalettes.isEmpty()) {
            colorRepository.insertDefaultPalettes()
        }
    }
}