package com.example.reminderassistant.ui.clipboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.reminderassistant.R

@Composable
fun ClipboardSuggestionCard(
    uiState: ClipboardSuggestionUiState,
    onReminder: () -> Unit,
    onCalendar: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.clipboard_suggestion_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = uiState.snippet,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = stringResource(R.string.clipboard_suggestion_type, uiState.suggestedType.name),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 6.dp)
            )
            Row(modifier = Modifier.padding(top = 12.dp)) {
                Button(onClick = onReminder, modifier = Modifier.padding(end = 8.dp)) {
                    Text(stringResource(R.string.clipboard_suggestion_reminder))
                }
                Button(onClick = onCalendar, modifier = Modifier.padding(end = 8.dp)) {
                    Text(stringResource(R.string.clipboard_suggestion_calendar))
                }
                Button(onClick = onDismiss) {
                    Text(stringResource(R.string.clipboard_suggestion_dismiss))
                }
            }
        }
    }
}
