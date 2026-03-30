package com.example.reminderassistant.domain.usecase

import com.example.reminderassistant.domain.model.ReminderItem
import com.example.reminderassistant.system.notification.ReminderScheduler
import javax.inject.Inject

class ScheduleReminderUseCase @Inject constructor(
    private val reminderScheduler: ReminderScheduler
) {
    operator fun invoke(reminder: ReminderItem) {
        reminderScheduler.schedule(reminder)
    }
}
