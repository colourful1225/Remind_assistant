package com.example.reminderassistant.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarEditorViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarEditorUiState())
    val uiState: StateFlow<CalendarEditorUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateLocation(location: String) {
        _uiState.value = _uiState.value.copy(location = location)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun saveCalendarDraft(onComplete: () -> Unit) {
        viewModelScope.launch {
            // TODO: Implement calendar intent launching
            onComplete()
        }
    }
}
