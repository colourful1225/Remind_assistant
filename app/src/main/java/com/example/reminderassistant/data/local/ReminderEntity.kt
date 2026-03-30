package com.example.reminderassistant.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.reminderassistant.domain.model.ReminderStatus
import com.example.reminderassistant.domain.model.SourceType

/**
 * Room entity for storing reminders in the local database
 */
@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "note")
    val note: String = "",

    @ColumnInfo(name = "reminder_time")
    val reminderTime: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "source_type")
    val sourceType: String = SourceType.MANUAL.name,

    @ColumnInfo(name = "source_app_package")
    val sourceAppPackage: String? = null,

    @ColumnInfo(name = "source_app_name")
    val sourceAppName: String? = null,

    @ColumnInfo(name = "raw_text")
    val rawText: String = "",

    @ColumnInfo(name = "status")
    val status: String = ReminderStatus.ACTIVE.name,

    @ColumnInfo(name = "tags_serialized")
    val tagsSerialized: String = ""
)
