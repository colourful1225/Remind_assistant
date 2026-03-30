package com.example.reminderassistant.ui.clipboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reminderassistant.navigation.Routes

@Composable
fun ClipboardSuggestionHost(
    viewModel: ClipboardSuggestionViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value
    if (!uiState.isVisible) return

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Box(modifier = Modifier.padding(16.dp)) {
            ClipboardSuggestionCard(
                uiState = uiState,
                onReminder = { viewModel.acceptSuggestion(Routes.REMINDER_EDITOR) },
                onCalendar = { viewModel.acceptSuggestion(Routes.CALENDAR_EDITOR) },
                onDismiss = { viewModel.dismiss() }
            )
        }
    }
}
