package com.example.reminderassistant.system.accessibility

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AccessibilityNodeTextExtractor {
    fun extract(event: AccessibilityEvent, root: AccessibilityNodeInfo?): String? {
        val eventText = event.text?.firstOrNull()?.toString()
        if (!eventText.isNullOrBlank()) return eventText

        val sourceText = event.source?.text?.toString()
        if (!sourceText.isNullOrBlank()) return sourceText

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
            if (!text.isNullOrBlank() && text.length in MIN_LENGTH..MAX_LENGTH) {
                return text
            }
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }
        return null
    }

    companion object {
        private const val MIN_LENGTH = 6
        private const val MAX_LENGTH = 160
        private const val MAX_NODES = 60
    }
}
