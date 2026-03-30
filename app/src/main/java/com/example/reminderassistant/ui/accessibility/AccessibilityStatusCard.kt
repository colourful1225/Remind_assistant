package com.example.reminderassistant.ui.accessibility

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reminderassistant.R
import androidx.compose.ui.res.stringResource

@Composable
fun AccessibilityStatusCard(
    enabled: Boolean,
    onOpenSettings: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.settings_accessibility),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = if (enabled) {
                    stringResource(R.string.settings_accessibility_enabled)
                } else {
                    stringResource(R.string.settings_accessibility_disabled)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            )
            Button(onClick = onOpenSettings) {
                Text(stringResource(R.string.settings_accessibility_open))
            }
        }
    }
}
