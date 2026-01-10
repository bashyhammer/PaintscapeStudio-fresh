package com.paintscape.studio

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PaintscapeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize AdMob SDK
        MobileAds.initialize(this) {}
    }
}