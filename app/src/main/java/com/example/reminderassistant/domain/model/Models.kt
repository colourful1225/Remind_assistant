package com.example.reminderassistant.domain.model

// Enums
enum class SourceType {
    SHARE,
    PROCESS_TEXT,
    CLIPBOARD,
    ACCESSIBILITY,
    MANUAL
}

enum class ParsedType {
    TODO,
    EVENT,
    UNKNOWN
}

enum class ReminderStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED
}

// Domain Models

/**
 * 表示一个待办/提醒项
 */
data class ReminderItem(
    val id: Long = 0,
    val title: String,
    val note: String = "",
    val reminderTime: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val sourceType: SourceType = SourceType.MANUAL,
    val sourceAppPackage: String? = null,
    val sourceAppName: String? = null,
    val rawText: String = "",
    val status: ReminderStatus = ReminderStatus.ACTIVE,
    val tags: List<String> = emptyList()
)

/**
 * 表示一个待创建的日历事件草稿
 */
data class CalendarDraft(
    val title: String,
    val description: String = "",
    val startTime: Long? = null,
    val endTime: Long? = null,
    val allDay: Boolean = false,
    val location: String? = null,
    val recurrenceRule: String? = null,
    val reminderOffsetMinutes: Int? = null
)

/**
 * 表示文本解析结果
 */
data class ParsedContent(
    val rawText: String,
    val cleanedText: String,
    val suggestedTitle: String,
    val detectedType: ParsedType,
    val detectedTime: Long? = null,
    val detectedEndTime: Long? = null,
    val detectedLocation: String? = null,
    val confidenceScore: Float = 0f,
    val matchedKeywords: List<String> = emptyList()
)

/**
 * 表示文本来源上下文
 */
data class SourceContext(
    val sourceType: SourceType,
    val sourceAppPackage: String? = null,
    val sourceAppName: String? = null,
    val receivedAt: Long = System.currentTimeMillis()
)

/**
 * 表示外部导入请求
 */
data class ImportRequest(
    val text: String,
    val sourceContext: SourceContext
)
