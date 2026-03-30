package com.example.reminderassistant.system.share

import android.content.Context
import com.example.reminderassistant.domain.model.ImportSession
import com.example.reminderassistant.domain.model.ParseResult
import com.example.reminderassistant.domain.model.ParsedType
import com.example.reminderassistant.domain.model.SourceContext
import com.example.reminderassistant.domain.model.SourceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportSessionStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _session = MutableStateFlow<ImportSession?>(null)
    val session: StateFlow<ImportSession?> = _session.asStateFlow()
    private val _preferredRoute = MutableStateFlow<String?>(null)
    val preferredRoute: StateFlow<String?> = _preferredRoute.asStateFlow()

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        restoreFromPrefs()
    }

    fun set(session: ImportSession, preferredRoute: String? = null) {
        _session.value = session
        _preferredRoute.value = preferredRoute
        saveToPrefs(session, preferredRoute)
    }

    fun clear() {
        _session.value = null
        _preferredRoute.value = null
        prefs.edit().clear().apply()
    }

    private fun saveToPrefs(session: ImportSession, preferredRoute: String?) {
        prefs.edit()
            .putString(KEY_RAW_TEXT, session.request.text)
            .putString(KEY_SOURCE_TYPE, session.request.sourceContext.sourceType.name)
            .putString(KEY_SOURCE_APP_PKG, session.request.sourceContext.sourceAppPackage)
            .putString(KEY_SOURCE_APP_NAME, session.request.sourceContext.sourceAppName)
            .putString(KEY_PARSE_CLEANED, session.parseResult.cleanedText)
            .putString(KEY_PARSE_TITLE, session.parseResult.suggestedTitle)
            .putString(KEY_PARSE_TYPE, session.parseResult.suggestedType.name)
            .putString(KEY_PARSE_TIME_TEXT, session.parseResult.detectedTimeText)
            .putString(KEY_PARSE_LOCATION, session.parseResult.detectedLocation)
            .putLong(KEY_PARSE_TIME_MILLIS, session.parseResult.detectedTimeMillis ?: -1L)
            .putFloat(KEY_PARSE_CONFIDENCE, session.parseResult.confidence)
            .putString(KEY_PREFERRED_ROUTE, preferredRoute)
            .apply()
    }

    private fun restoreFromPrefs() {
        val rawText = prefs.getString(KEY_RAW_TEXT, null) ?: return
        val sourceTypeName = prefs.getString(KEY_SOURCE_TYPE, null) ?: return
        val sourceType = runCatching { SourceType.valueOf(sourceTypeName) }.getOrNull() ?: return
        val parseTypeName = prefs.getString(KEY_PARSE_TYPE, null) ?: ParsedType.UNKNOWN.name
        val parseType = runCatching { ParsedType.valueOf(parseTypeName) }.getOrDefault(ParsedType.UNKNOWN)
        val timeMillis = prefs.getLong(KEY_PARSE_TIME_MILLIS, -1L).let { if (it == -1L) null else it }

        val parseResult = ParseResult(
            rawText = rawText,
            cleanedText = prefs.getString(KEY_PARSE_CLEANED, rawText).orEmpty(),
            suggestedTitle = prefs.getString(KEY_PARSE_TITLE, "").orEmpty(),
            suggestedType = parseType,
            detectedTimeMillis = timeMillis,
            detectedTimeText = prefs.getString(KEY_PARSE_TIME_TEXT, null),
            detectedLocation = prefs.getString(KEY_PARSE_LOCATION, null),
            confidence = prefs.getFloat(KEY_PARSE_CONFIDENCE, 0f)
        )

        val sourceContext = SourceContext(
            sourceType = sourceType,
            sourceAppPackage = prefs.getString(KEY_SOURCE_APP_PKG, null),
            sourceAppName = prefs.getString(KEY_SOURCE_APP_NAME, null)
        )

        _session.value = ImportSession(
            request = com.example.reminderassistant.domain.model.ImportRequest(
                text = rawText,
                sourceContext = sourceContext
            ),
            parseResult = parseResult
        )
        _preferredRoute.value = prefs.getString(KEY_PREFERRED_ROUTE, null)
    }

    companion object {
        private const val PREFS_NAME = "import_session_store"
        private const val KEY_RAW_TEXT = "raw_text"
        private const val KEY_SOURCE_TYPE = "source_type"
        private const val KEY_SOURCE_APP_PKG = "source_app_pkg"
        private const val KEY_SOURCE_APP_NAME = "source_app_name"
        private const val KEY_PARSE_CLEANED = "parse_cleaned"
        private const val KEY_PARSE_TITLE = "parse_title"
        private const val KEY_PARSE_TYPE = "parse_type"
        private const val KEY_PARSE_TIME_TEXT = "parse_time_text"
        private const val KEY_PARSE_TIME_MILLIS = "parse_time_millis"
        private const val KEY_PARSE_LOCATION = "parse_location"
        private const val KEY_PARSE_CONFIDENCE = "parse_confidence"
        private const val KEY_PREFERRED_ROUTE = "preferred_route"
    }
}
