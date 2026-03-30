package com.example.reminderassistant.data.mapper

import com.example.reminderassistant.data.local.ReminderEntity
import com.example.reminderassistant.domain.model.ReminderItem
import com.example.reminderassistant.domain.model.ReminderStatus
import com.example.reminderassistant.domain.model.SourceType
import javax.inject.Inject

/**
 * Mapper for converting between ReminderEntity and ReminderItem
 */
class ReminderMapper @Inject constructor() {

    fun entityToDomain(entity: ReminderEntity): ReminderItem {
        return ReminderItem(
            id = entity.id,
            title = entity.title,
            note = entity.note,
            reminderTime = entity.reminderTime,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            sourceType = SourceType.valueOf(entity.sourceType),
            sourceAppPackage = entity.sourceAppPackage,
            sourceAppName = entity.sourceAppName,
            rawText = entity.rawText,
            status = ReminderStatus.valueOf(entity.status),
            tags = if (entity.tagsSerialized.isEmpty()) emptyList() else entity.tagsSerialized.split(",")
        )
    }

    fun domainToEntity(item: ReminderItem): ReminderEntity {
        return ReminderEntity(
            id = item.id,
            title = item.title,
            note = item.note,
            reminderTime = item.reminderTime,
            createdAt = item.createdAt,
            updatedAt = item.updatedAt,
            sourceType = item.sourceType.name,
            sourceAppPackage = item.sourceAppPackage,
            sourceAppName = item.sourceAppName,
            rawText = item.rawText,
            status = item.status.name,
            tagsSerialized = item.tags.joinToString(",")
        )
    }

    fun entityListToDomain(entities: List<ReminderEntity>): List<ReminderItem> {
        return entities.map { entityToDomain(it) }
    }
}
