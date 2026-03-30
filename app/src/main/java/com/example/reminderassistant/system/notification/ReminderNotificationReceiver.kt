package com.example.reminderassistant.system.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val reminderId = intent?.getLongExtra(EXTRA_REMINDER_ID, 0L) ?: 0L
        val title = intent?.getStringExtra(EXTRA_TITLE) ?: "Reminder"
        val note = intent?.getStringExtra(EXTRA_NOTE) ?: ""

        NotificationHelper.showReminderNotification(
            context = context,
            reminderId = reminderId,
            title = title,
            note = note
        )
    }

    companion object {
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_NOTE = "extra_note"
    }
}
