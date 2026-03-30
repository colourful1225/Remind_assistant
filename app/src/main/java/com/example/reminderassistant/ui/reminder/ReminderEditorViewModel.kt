package com.example.reminderassistant.ui.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderassistant.domain.model.ReminderItem
import com.example.reminderassistant.domain.usecase.CreateReminderUseCase
import com.example.reminderassistant.system.share.ImportSessionStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderEditorViewModel @Inject constructor(
    private val createReminderUseCase: CreateReminderUseCase,
    private val importSessionStore: ImportSessionStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderEditorUiState())
    val uiState: StateFlow<ReminderEditorUiState> = _uiState.asStateFlow()

    init {
        val session = importSessionStore.session.value
        if (session != null && _uiState.value.title.isBlank()) {
            _uiState.value = _uiState.value.copy(
                title = session.parseResult.suggestedTitle,
                note = session.parseResult.rawText
            )
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateNote(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun saveReminder(onComplete: () -> Unit) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.title.isNotEmpty()) {
                val reminderItem = ReminderItem(
                    title = currentState.title,
                    note = currentState.note
                )
                createReminderUseCase(reminderItem)
                importSessionStore.clear()
                onComplete()
            }
        }
    }
}
