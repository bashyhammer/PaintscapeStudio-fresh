package com.paintscape.studio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.paintscape.studio.ui.navigation.PaintscapeNavHost
import com.paintscape.studio.ui.screens.settings.SettingsViewModel
import com.paintscape.studio.ui.theme.PaintscapeStudioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Get the settings ViewModel to observe dark theme preference
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

            // Apply the theme based on user preference
            PaintscapeStudioTheme(darkTheme = isDarkTheme) {
                PaintscapeNavHost()
            }
        }
    }
}