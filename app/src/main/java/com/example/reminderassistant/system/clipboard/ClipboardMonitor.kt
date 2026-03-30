package com.example.reminderassistant.system.clipboard

import kotlinx.coroutines.flow.Flow

interface ClipboardMonitor {
    fun clipboardTextFlow(): Flow<String>
}
