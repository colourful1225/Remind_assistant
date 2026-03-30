package com.example.reminderassistant.domain.usecase

import com.example.reminderassistant.domain.model.ReminderItem
import com.example.reminderassistant.domain.model.ReminderStatus
import com.example.reminderassistant.domain.repository.ReminderRepository
import com.example.reminderassistant.system.notification.ReminderScheduler
import javax.inject.Inject

class CompleteReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke(reminder: ReminderItem) {
        val updated = reminder.copy(status = ReminderStatus.COMPLETED)
        reminderRepository.updateReminder(updated)
        reminderScheduler.cancel(reminder.id)
    }
}
