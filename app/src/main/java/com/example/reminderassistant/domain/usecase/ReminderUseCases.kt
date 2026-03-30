package com.example.reminderassistant.domain.usecase

import com.example.reminderassistant.domain.model.ReminderItem
import com.example.reminderassistant.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase to fetch all reminders
 */
class GetAllRemindersUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    operator fun invoke(): Flow<List<ReminderItem>> {
        return reminderRepository.getAllReminders()
    }
}

/**
 * UseCase to create a new reminder
 */
class CreateReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(item: ReminderItem): Long {
        return reminderRepository.createReminder(item)
    }
}

/**
 * UseCase to update a reminder
 */
class UpdateReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(item: ReminderItem) {
        reminderRepository.updateReminder(item)
    }
}

/**
 * UseCase to delete a reminder
 */
class DeleteReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(item: ReminderItem) {
        reminderRepository.deleteReminder(item)
    }
}
