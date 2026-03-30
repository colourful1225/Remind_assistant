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
    private val _preferredRoute = MutableStateFlow<String?>(null)
    val preferredRoute: StateFlow<String?> = _preferredRoute.asStateFlow()

    fun set(session: ImportSession, preferredRoute: String? = null) {
        _session.value = session
        _preferredRoute.value = preferredRoute
    }

    fun clear() {
        _session.value = null
        _preferredRoute.value = null
    }
}
