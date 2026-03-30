package com.example.reminderassistant.data.repository

import com.example.reminderassistant.data.local.ReminderDao
import com.example.reminderassistant.data.mapper.ReminderMapper
import com.example.reminderassistant.domain.model.ReminderItem
import com.example.reminderassistant.domain.model.ReminderStatus
import com.example.reminderassistant.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of ReminderRepository
 */
class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao,
    private val reminderMapper: ReminderMapper
) : ReminderRepository {

    override fun getAllReminders(): Flow<List<ReminderItem>> {
        return reminderDao.getAllReminders().map { entities ->
            reminderMapper.entityListToDomain(entities)
        }
    }

    override suspend fun createReminder(item: ReminderItem): Long {
        val entity = reminderMapper.domainToEntity(item)
        return reminderDao.insertReminder(entity)
    }

    override suspend fun updateReminder(item: ReminderItem) {
        val entity = reminderMapper.domainToEntity(item)
        reminderDao.updateReminder(entity)
    }

    override suspend fun deleteReminder(item: ReminderItem) {
        val entity = reminderMapper.domainToEntity(item)
        reminderDao.deleteReminder(entity)
    }

    override fun getReminderById(id: Long): Flow<ReminderItem?> {
        return reminderDao.getReminderById(id).map { entity ->
            entity?.let { reminderMapper.entityToDomain(it) }
        }
    }

    override suspend fun getActiveRemindersAfter(now: Long): List<ReminderItem> {
        return reminderDao.getActiveRemindersAfter(
            now = now,
            status = ReminderStatus.ACTIVE.name
        ).map { reminderMapper.entityToDomain(it) }
    }
}
