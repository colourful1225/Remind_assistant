package com.example.reminderassistant.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Reminder operations
 */
@Dao
interface ReminderDao {
    /**
     * Get all reminders ordered by creation time
     */
    @Query("SELECT * FROM reminders ORDER BY created_at DESC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    /**
     * Get a reminder by ID
     */
    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getReminderById(id: Long): Flow<ReminderEntity?>

    /**
     * Insert a new reminder
     */
    @Insert
    suspend fun insertReminder(entity: ReminderEntity): Long

    /**
     * Update an existing reminder
     */
    @Update
    suspend fun updateReminder(entity: ReminderEntity)

    /**
     * Delete a reminder
     */
    @Delete
    suspend fun deleteReminder(entity: ReminderEntity)

    /**
     * Delete all reminders (for testing purposes)
     */
    @Query("DELETE FROM reminders")
    suspend fun deleteAllReminders()
}
