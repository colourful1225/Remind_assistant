package com.example.reminderassistant.system.intent

import android.content.Context
import android.content.Intent
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 小米设备的日历和备忘录 Intent 调用
 */
class XiaomiIntentCaller(private val context: Context) {

    /**
     * 打开小米日历，创建新事件
     * @param title 事件标题
     * @param timeMillis 事件时间戳（毫秒）
     * @param description 事件描述
     */
    fun startMiCalendar(title: String, timeMillis: Long?, description: String = "") {
        val intent = Intent().apply {
            action = "android.intent.action.INSERT"
            type = "vnd.android.cursor.item/event"
            
            // 小米日历包名和类名
            setClassName("com.android.calendar", "com.android.calendar.event.EditEventActivity")
            
            putExtra("title", title)
            if (description.isNotEmpty()) {
                putExtra("description", description)
            }
            if (timeMillis != null) {
                // 转换毫秒为秒
                putExtra("beginTime", timeMillis)
            }
            
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // 备用方案：使用通用日历 Intent
            startGenericCalendar(title, timeMillis, description)
        }
    }

    /**
     * 打开小米备忘录，创建新备忘
     * @param title 备忘标题
     * @param content 备忘内容
     */
    fun startMiNotes(title: String, content: String = "") {
        val intent = Intent().apply {
            action = "android.intent.action.INSERT"
            type = "vnd.android.cursor.item/mNote"
            
            // 小米备忘录包名
            setClassName("com.xiaomi.notes", "com.xiaomi.notes.activity.NewNoteActivity")
            
            putExtra("title", title)
            if (content.isNotEmpty()) {
                putExtra("content", content)
            }
            
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // 备用方案：使用系统备忘录
            startGenericNotes(title, content)
        }
    }

    /**
     * 通用日历 Intent（备用）
     */
    private fun startGenericCalendar(title: String, timeMillis: Long?, description: String) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = "vnd.android.cursor.item/event"
            putExtra("title", title)
            if (description.isNotEmpty()) {
                putExtra("description", description)
            }
            if (timeMillis != null) {
                putExtra("beginTime", timeMillis)
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // 无可用日历应用
        }
    }

    /**
     * 通用备忘录 Intent（备用）
     */
    private fun startGenericNotes(title: String, content: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // 无可用备忘应用
        }
    }

    /**
     * 将时间戳转换为格式化字符串
     */
    fun formatTimestamp(timeMillis: Long): String {
        return try {
            val instant = Instant.ofEpochMilli(timeMillis)
            val formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")
                .withZone(ZoneId.systemDefault())
            formatter.format(instant)
        } catch (e: Exception) {
            ""
        }
    }
}
