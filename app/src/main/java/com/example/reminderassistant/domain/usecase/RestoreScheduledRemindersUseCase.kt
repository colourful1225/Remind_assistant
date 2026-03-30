package com.example.reminderassistant.domain.usecase

import com.example.reminderassistant.domain.repository.ReminderRepository
import com.example.reminderassistant.system.notification.ReminderScheduler
import javax.inject.Inject

class RestoreScheduledRemindersUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke() {
        val now = System.currentTimeMillis()
        val reminders = reminderRepository.getActiveRemindersAfter(now)
        reminders.forEach { reminderScheduler.schedule(it) }
    }
}
