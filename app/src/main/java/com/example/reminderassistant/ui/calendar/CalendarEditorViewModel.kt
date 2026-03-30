package com.example.reminderassistant.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderassistant.domain.usecase.BuildCalendarDraftUseCase
import com.example.reminderassistant.system.calendar.CalendarIntentLauncher
import com.example.reminderassistant.system.calendar.CalendarLaunchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.reminderassistant.system.share.ImportSessionStore

sealed class CalendarUiEvent {
    data class LaunchCalendar(val intent: android.content.Intent) : CalendarUiEvent()
}

@HiltViewModel
class CalendarEditorViewModel @Inject constructor(
    private val importSessionStore: ImportSessionStore,
    private val buildCalendarDraftUseCase: BuildCalendarDraftUseCase,
    private val calendarIntentLauncher: CalendarIntentLauncher
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarEditorUiState())
    val uiState: StateFlow<CalendarEditorUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<CalendarUiEvent>()
    val events = _events.asSharedFlow()

    init {
        val session = importSessionStore.session.value
        if (session != null && _uiState.value.title.isBlank()) {
            val start = session.parseResult.detectedTimeMillis
            _uiState.value = _uiState.value.copy(
                title = session.parseResult.suggestedTitle,
                description = session.parseResult.rawText,
                location = session.parseResult.detectedLocation.orEmpty(),
                startTime = start,
                endTime = defaultEndTime(start, false),
                rawText = session.parseResult.rawText
            )
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title, launchError = null)
    }

    fun updateLocation(location: String) {
        _uiState.value = _uiState.value.copy(location = location, launchError = null)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description, launchError = null)
    }

    fun updateStartTime(timeMillis: Long) {
        val endTime = if (_uiState.value.endTime == null || _uiState.value.endTime!! <= timeMillis) {
            defaultEndTime(timeMillis, _uiState.value.allDay)
        } else {
            _uiState.value.endTime
        }
        _uiState.value = _uiState.value.copy(
            startTime = timeMillis,
            endTime = endTime,
            launchError = null
        )
    }

    fun updateEndTime(timeMillis: Long) {
        _uiState.value = _uiState.value.copy(endTime = timeMillis, launchError = null)
    }

    fun updateAllDay(isAllDay: Boolean) {
        val start = _uiState.value.startTime
        val end = if (start != null) defaultEndTime(start, isAllDay) else _uiState.value.endTime
        _uiState.value = _uiState.value.copy(
            allDay = isAllDay,
            endTime = end,
            launchError = null
        )
    }

    fun launchCalendar() {
        viewModelScope.launch {
            val current = _uiState.value
            val draft = buildCalendarDraftUseCase.build(
                title = current.title,
                description = current.description,
                startTime = current.startTime,
                endTime = current.endTime,
                allDay = current.allDay,
                location = current.location.ifBlank { null },
                sourceRawText = current.rawText
            )

            when (val result = calendarIntentLauncher.prepareLaunch(draft)) {
                is CalendarLaunchResult.Ready -> {
                    importSessionStore.clear()
                    _uiState.value = current.copy(launchError = null)
                    _events.emit(CalendarUiEvent.LaunchCalendar(result.intent))
                }
                CalendarLaunchResult.NoCalendarApp -> {
                    _uiState.value = current.copy(launchError = CalendarLaunchError.NO_CALENDAR_APP)
                }
            }
        }
    }

    private fun defaultEndTime(startTime: Long?, allDay: Boolean): Long? {
        if (startTime == null) return null
        val durationMs = if (allDay) 24 * 60 * 60 * 1000L else 60 * 60 * 1000L
        return startTime + durationMs
    }
}
