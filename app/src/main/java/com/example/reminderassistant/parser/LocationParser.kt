package com.example.reminderassistant.parser

import javax.inject.Inject

class LocationParser @Inject constructor() {
    fun parse(text: String): String? {
        val locationRegex = Regex("(地点|地址)[:：]?\\s*([^，。\\n]+)")
        val locationMatch = locationRegex.find(text)
        if (locationMatch != null) {
            return locationMatch.groupValues[2].trim()
        }

        val inRegex = Regex("在([^，。\\n]+)")
        val inMatch = inRegex.find(text)
        if (inMatch != null) {
            return inMatch.groupValues[1].trim()
        }

        return null
    }
}
