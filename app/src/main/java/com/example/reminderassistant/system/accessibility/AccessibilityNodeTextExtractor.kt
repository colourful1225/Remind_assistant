package com.example.reminderassistant.system.accessibility

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AccessibilityNodeTextExtractor {
    fun extract(event: AccessibilityEvent, root: AccessibilityNodeInfo?): String? {
        val eventText = event.text?.firstOrNull()?.toString()
        if (isValidCandidate(eventText)) return eventText

        val sourceText = event.source?.text?.toString()
        if (isValidCandidate(sourceText)) return sourceText

        if (root == null) return null
        return traverseForText(root)
    }

    private fun traverseForText(root: AccessibilityNodeInfo): String? {
        val queue: ArrayDeque<AccessibilityNodeInfo> = ArrayDeque()
        queue.add(root)
        var scanned = 0

        while (queue.isNotEmpty() && scanned < MAX_NODES) {
            val node = queue.removeFirst()
            scanned++
            val text = node.text?.toString()
            if (isValidCandidate(text)) {
                return text
            }
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }
        return null
    }

    private fun isValidCandidate(text: String?): Boolean {
        if (text.isNullOrBlank()) return false
        return text.length in MIN_LENGTH..MAX_LENGTH
    }

    companion object {
        private const val MIN_LENGTH = 1
        private const val MAX_LENGTH = 300
        private const val MAX_NODES = 60
    }
}
