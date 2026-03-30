package com.example.reminderassistant.ui.accessibility

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
import androidx.compose.ui.unit.dp
import com.example.reminderassistant.R

@Composable
fun AccessibilityQuickActionCard(
    title: String,
    onReminder: () -> Unit,
    onCalendar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
        Row(modifier = Modifier.padding(16.dp)) {
            Button(onClick = onReminder, modifier = Modifier.padding(end = 8.dp)) {
                Text(stringResource(R.string.accessibility_add_reminder))
            }
            Button(onClick = onCalendar) {
                Text(stringResource(R.string.accessibility_add_calendar))
            }
        }
    }
}
