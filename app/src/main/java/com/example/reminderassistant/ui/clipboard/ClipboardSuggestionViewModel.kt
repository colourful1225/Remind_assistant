package com.example.reminderassistant.ui.clipboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderassistant.domain.model.ImportRequest
import com.example.reminderassistant.domain.model.ImportSession
import com.example.reminderassistant.domain.model.SourceContext
import com.example.reminderassistant.domain.model.SourceType
import com.example.reminderassistant.domain.model.SuggestionDecision
import com.example.reminderassistant.domain.usecase.DetectClipboardSuggestionUseCase
import com.example.reminderassistant.domain.usecase.DismissSuggestionUseCase
import com.example.reminderassistant.domain.usecase.RecordSuggestionShownUseCase
import com.example.reminderassistant.system.clipboard.ClipboardMonitor
import com.example.reminderassistant.system.share.ImportSessionStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClipboardSuggestionViewModel @Inject constructor(
    private val clipboardMonitor: ClipboardMonitor,
    private val detectClipboardSuggestionUseCase: DetectClipboardSuggestionUseCase,
    private val recordSuggestionShownUseCase: RecordSuggestionShownUseCase,
    private val dismissSuggestionUseCase: DismissSuggestionUseCase,
    private val importSessionStore: ImportSessionStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClipboardSuggestionUiState())
    val uiState: StateFlow<ClipboardSuggestionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            clipboardMonitor.clipboardTextFlow().collect { text ->
                if (_uiState.value.isVisible) return@collect
                when (val decision = detectClipboardSuggestionUseCase.invoke(text)) {
                    is SuggestionDecision.Allowed -> {
                        recordSuggestionShownUseCase.invoke(
                            hash = decision.suggestion.textHash,
                            timestamp = decision.suggestion.createdAt
                        )
                        _uiState.value = ClipboardSuggestionUiState(
                            isVisible = true,
                            rawText = decision.suggestion.rawText,
                            snippet = decision.suggestion.rawText.take(80),
                            suggestedType = decision.suggestion.suggestedType,
                            confidence = decision.suggestion.confidence,
                            textHash = decision.suggestion.textHash,
                            parseResult = decision.suggestion.parseResult
                        )
                    }
                    is SuggestionDecision.Blocked -> {
                        // ignore
                    }
                }
            }
        }
    }

    fun dismiss() {
        val current = _uiState.value
        if (!current.isVisible) return
        dismissSuggestionUseCase.invoke(current.textHash, System.currentTimeMillis())
        _uiState.value = ClipboardSuggestionUiState()
    }

    fun acceptSuggestion(preferredRoute: String) {
        val current = _uiState.value
        if (!current.isVisible) return
        val parseResult = current.parseResult ?: return
        val session = ImportSession(
            request = ImportRequest(
                text = current.rawText,
                sourceContext = SourceContext(SourceType.CLIPBOARD)
            ),
            parseResult = parseResult
        )
        importSessionStore.set(session, preferredRoute)
        _uiState.value = ClipboardSuggestionUiState()
    }
}
