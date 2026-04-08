package com.example.reminderassistant.system.intent

import android.content.Context
import android.content.Intent
import android.content.ClipData
import android.content.ClipboardManager
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class XiaomiIntentCaller(private val context: Context) {

    fun startMiCalendar(title: String, timeMillis: Long?, description: String = "") {
        val intent = Intent().apply {
            action = Intent.ACTION_INSERT
            type = "vnd.android.cursor.item/event"
            setClassName("com.android.calendar", "com.android.calendar.event.EditEventActivity")
            putExtra("title", title)
            if (description.isNotEmpty()) putExtra("description", description)
            if (timeMillis != null) putExtra("beginTime", timeMillis)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            startGenericCalendar(title, timeMillis, description)
        }
    }

    fun startMiNotes(title: String, content: String = "") {
        val todoText = when {
            content.isNotBlank() -> content.trim()
            title.isNotBlank() -> title.trim()
            else -> ""
        }

        copyToClipboard(title, todoText)

        try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                setPackage("com.miui.notes")
                type = "vnd.android.cursor.item/todo"
                putExtra("title", title)
                putExtra("content", todoText)
                putExtra(Intent.EXTRA_TEXT, todoText)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(intent)
            return
        } catch (_: Exception) {
        }

        try {
            val mainIntent = Intent().apply {
                setPackage("com.miui.notes")
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(mainIntent)
        } catch (_: Exception) {
            startGenericNotes(title, todoText)
        }
    }

    private fun copyToClipboard(title: String, content: String) {
        try {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipText = if (content.isNotBlank()) content else title
            val clip = ClipData.newPlainText("reminder_todo", clipText)
            clipboardManager.setPrimaryClip(clip)
        } catch (_: Exception) {
        }
    }

    private fun startGenericCalendar(title: String, timeMillis: Long?, description: String) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = "vnd.android.cursor.item/event"
            putExtra("title", title)
            if (description.isNotEmpty()) putExtra("description", description)
            if (timeMillis != null) putExtra("beginTime", timeMillis)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }

    private fun startGenericNotes(title: String, content: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }

    fun formatTimestamp(timeMillis: Long): String {
        return try {
            val instant = Instant.ofEpochMilli(timeMillis)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault())
            formatter.format(instant)
        } catch (_: Exception) {
            ""
        }
    }
}
