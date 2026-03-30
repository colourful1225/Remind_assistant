package com.example.reminderassistant.system.share

import com.example.reminderassistant.domain.model.ImportSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportSessionStore @Inject constructor() {
    private val _session = MutableStateFlow<ImportSession?>(null)
    val session: StateFlow<ImportSession?> = _session.asStateFlow()

    fun set(session: ImportSession) {
        _session.value = session
    }

    fun clear() {
        _session.value = null
    }
}
