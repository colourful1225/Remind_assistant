package com.example.reminderassistant.system.notification

import android.app.AlarmManager
import android.content.Context
import com.example.reminderassistant.domain.model.ReminderItem
import com.example.reminderassistant.domain.model.ReminderStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ReminderScheduler {

    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(reminder: ReminderItem) {
        val time = reminder.reminderTime ?: return
        if (reminder.status != ReminderStatus.ACTIVE) return
        if (time <= System.currentTimeMillis()) return

        val pendingIntent = ReminderIntentFactory.createReminderPendingIntent(context, reminder)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    override fun cancel(reminderId: Long) {
        val dummyReminder = ReminderItem(id = reminderId, title = "", note = "")
        val pendingIntent = ReminderIntentFactory.createReminderPendingIntent(context, dummyReminder)
        alarmManager.cancel(pendingIntent)
    }
}
