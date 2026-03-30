package com.example.reminderassistant.system.calendar

import android.content.Intent
import android.provider.CalendarContract
import com.example.reminderassistant.domain.model.CalendarDraft

object CalendarIntentFactory {
    fun createInsertIntent(draft: CalendarDraft): Intent {
        return Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, draft.title)
            putExtra(CalendarContract.Events.EVENT_LOCATION, draft.location)
            putExtra(CalendarContract.Events.DESCRIPTION, draft.description)

            if (draft.startTime != null) {
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, draft.startTime)
            }
            if (draft.endTime != null) {
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, draft.endTime)
            }
            if (draft.allDay) {
                putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
            }
        }
    }
}
