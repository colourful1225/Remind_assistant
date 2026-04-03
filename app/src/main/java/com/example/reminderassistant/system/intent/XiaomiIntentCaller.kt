package com.example.reminderassistant.system.intent

import android.content.Context
import android.content.Intent
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

        val primaryIntent = Intent("com.miui.todo.shortcut.action.INSERT_OR_EDIT").apply {
            setPackage("com.miui.notes")
            putExtra("title", title)
            putExtra("content", todoText)
            putExtra("text", todoText)
            putExtra("todo_content", todoText)
            putExtra("com.miui.todo.intent.extra.CONTENT", todoText)
            putExtra("com.miui.todo.intent.extra.TEXT", todoText)
            putExtra(Intent.EXTRA_TEXT, todoText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val secondaryIntent = Intent("com.miui.todo.action.INSERT_OR_EDIT").apply {
            setPackage("com.miui.notes")
            putExtra("title", title)
            putExtra("content", todoText)
            putExtra("text", todoText)
            putExtra("todo_content", todoText)
            putExtra("com.miui.todo.intent.extra.CONTENT", todoText)
            putExtra("com.miui.todo.intent.extra.TEXT", todoText)
            putExtra(Intent.EXTRA_TEXT, todoText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val tertiaryIntent = Intent(Intent.ACTION_SEND).apply {
            setPackage("com.miui.notes")
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, todoText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(primaryIntent)
        } catch (_: Exception) {
            try {
                context.startActivity(secondaryIntent)
            } catch (_: Exception) {
                try {
                    context.startActivity(tertiaryIntent)
                } catch (_: Exception) {
                    startGenericNotes(title, todoText)
                }
            }
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
