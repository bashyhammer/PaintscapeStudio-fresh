package com.paintscape.studio.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintscape.studio.data.repository.BillingRepository
import com.paintscape.studio.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val isSoundEnabled: Boolean = true,
    val isPremium: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val billingRepository: BillingRepository
) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean> = settingsRepository.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val state: StateFlow<SettingsUiState> = combine(
        settingsRepository.isDarkMode,
        settingsRepository.isSoundEnabled,
        billingRepository.premiumStatus
    ) { isDark, isSound, isPremium ->
        SettingsUiState(
            isDarkMode = isDark,
            isSoundEnabled = isSound,
            isPremium = isPremium
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun toggleDarkMode(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(isEnabled)
        }
    }

    fun toggleSound(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSoundEnabled(isEnabled)
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            billingRepository.restorePurchases()
            // Optional: Add logging or UI feedback for the user
        }
    }
}