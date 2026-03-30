package com.example.reminderassistant.system.notification

import com.example.reminderassistant.domain.model.ReminderItem

interface ReminderScheduler {
    fun schedule(reminder: ReminderItem)
    fun cancel(reminderId: Long)
}
