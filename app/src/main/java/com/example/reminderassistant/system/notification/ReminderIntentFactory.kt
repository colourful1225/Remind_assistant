package com.example.reminderassistant.system.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.reminderassistant.system.notification.ReminderNotificationReceiver.Companion.EXTRA_NOTE
import com.example.reminderassistant.system.notification.ReminderNotificationReceiver.Companion.EXTRA_REMINDER_ID
import com.example.reminderassistant.system.notification.ReminderNotificationReceiver.Companion.EXTRA_TITLE
import com.example.reminderassistant.domain.model.ReminderItem

object ReminderIntentFactory {
    fun createReminderPendingIntent(context: Context, reminder: ReminderItem): PendingIntent {
        val intent = Intent(context, ReminderNotificationReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, reminder.id)
            putExtra(EXTRA_TITLE, reminder.title)
            putExtra(EXTRA_NOTE, reminder.note)
        }
        val requestCode = reminder.id.hashCode()
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
