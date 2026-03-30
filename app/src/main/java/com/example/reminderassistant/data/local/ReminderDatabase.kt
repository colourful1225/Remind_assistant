package com.example.reminderassistant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for Reminder Assistant
 */
@Database(entities = [ReminderEntity::class], version = 1, exportSchema = false)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao

    companion object {
        const val DATABASE_NAME = "reminder_db"
    }
}
