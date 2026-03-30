package com.example.reminderassistant.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleClipboardSuggestions() {
        val current = _uiState.value
        _uiState.value = current.copy(
            clipboardSuggestionsEnabled = !current.clipboardSuggestionsEnabled
        )
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
