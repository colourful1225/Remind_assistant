package com.example.reminderassistant.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderassistant.domain.model.ReminderItem
import com.example.reminderassistant.domain.usecase.CompleteReminderUseCase
import com.example.reminderassistant.domain.usecase.DeleteReminderUseCase
import com.example.reminderassistant.domain.usecase.GetAllRemindersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getAllRemindersUseCase: GetAllRemindersUseCase,
    private val completeReminderUseCase: CompleteReminderUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = getAllRemindersUseCase()
        .map { reminders ->
            HomeUiState(
                isLoading = false,
                reminders = reminders
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(isLoading = true, reminders = emptyList())
        )

    fun markComplete(reminder: ReminderItem) {
        viewModelScope.launch {
            completeReminderUseCase(reminder)
        }
    }

    fun deleteReminder(reminder: ReminderItem) {
        viewModelScope.launch {
            deleteReminderUseCase(reminder)
        }
    }
}
