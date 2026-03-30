package com.example.reminderassistant.domain.repository

import com.example.reminderassistant.domain.model.ReminderItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Reminder operations
 */
interface ReminderRepository {
    /**
     * Get all reminders as a Flow
     */
    fun getAllReminders(): Flow<List<ReminderItem>>

    /**
     * Create a new reminder
     */
    suspend fun createReminder(item: ReminderItem): Long

    /**
     * Update an existing reminder
     */
    suspend fun updateReminder(item: ReminderItem)

    /**
     * Delete a reminder
     */
    suspend fun deleteReminder(item: ReminderItem)

    /**
     * Get a specific reminder by ID
     */
    fun getReminderById(id: Long): Flow<ReminderItem?>
}
