package com.example.reminderassistant.ui.settings

import androidx.lifecycle.ViewModel
import com.example.reminderassistant.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            clipboardSuggestionsEnabled = settingsRepository.getClipboardSuggestionsEnabled(),
            clipboardCooldownMinutes = settingsRepository.getCooldownMinutes(),
            highConfidenceOnly = settingsRepository.getHighConfidenceOnly()
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleClipboardSuggestions() {
        val current = _uiState.value
        settingsRepository.setClipboardSuggestionsEnabled(!current.clipboardSuggestionsEnabled)
        _uiState.value = current.copy(
            clipboardSuggestionsEnabled = !current.clipboardSuggestionsEnabled
        )
    }

    fun setCooldownMinutes(minutes: Int) {
        settingsRepository.setCooldownMinutes(minutes)
        _uiState.value = _uiState.value.copy(clipboardCooldownMinutes = minutes)
    }

    fun toggleHighConfidenceOnly() {
        val current = _uiState.value
        settingsRepository.setHighConfidenceOnly(!current.highConfidenceOnly)
        _uiState.value = current.copy(highConfidenceOnly = !current.highConfidenceOnly)
    }

    fun toggleAccessibilityService() {
        val current = _uiState.value
        _uiState.value = current.copy(
            accessibilityServiceEnabled = !current.accessibilityServiceEnabled
        )
    }

    fun toggleShowSourceApp() {
        val current = _uiState.value
        _uiState.value = current.copy(
            showSourceApp = !current.showSourceApp
        )
    }
}
