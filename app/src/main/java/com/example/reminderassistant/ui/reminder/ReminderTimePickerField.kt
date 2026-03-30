package com.example.reminderassistant.ui.reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.reminderassistant.R
import com.example.reminderassistant.util.DateTimeFormatter
import java.util.Calendar

@Composable
fun ReminderTimePickerField(
    reminderTime: Long?,
    onTimeSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    val displayText = reminderTime?.let { DateTimeFormatter.format(it) }
        ?: stringResource(R.string.reminder_time_unset)

    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(R.string.reminder_time)) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val now = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                val calendar = Calendar.getInstance()
                                calendar.set(year, month, day, hour, minute, 0)
                                onTimeSelected(calendar.timeInMillis)
                            },
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
    )

    if (reminderTime == null) {
        Text(
            text = stringResource(R.string.reminder_time_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
