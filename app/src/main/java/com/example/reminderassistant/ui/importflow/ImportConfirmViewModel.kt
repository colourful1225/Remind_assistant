package com.example.reminderassistant.ui.importflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderassistant.system.share.ImportSessionStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ImportConfirmViewModel @Inject constructor(
    private val importSessionStore: ImportSessionStore
) : ViewModel() {

    val uiState: StateFlow<ImportUiState> = importSessionStore.session
        .map { session ->
            if (session == null) {
                ImportUiState()
            } else {
                ImportUiState(
                    rawText = session.parseResult.rawText,
                    cleanedText = session.parseResult.cleanedText,
                    suggestedTitle = session.parseResult.suggestedTitle,
                    suggestedType = session.parseResult.suggestedType,
                    detectedTimeText = session.parseResult.detectedTimeText,
                    detectedTimeMillis = session.parseResult.detectedTimeMillis,
                    detectedLocation = session.parseResult.detectedLocation,
                    confidence = session.parseResult.confidence,
                    isEmpty = false
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ImportUiState()
        )

    fun clearSession() {
        importSessionStore.clear()
    }
}
