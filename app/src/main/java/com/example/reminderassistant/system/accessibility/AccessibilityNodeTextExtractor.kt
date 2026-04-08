package com.example.reminderassistant.system.accessibility

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AccessibilityNodeTextExtractor {
    fun extract(event: AccessibilityEvent, root: AccessibilityNodeInfo?): String? {
        val eventBest = event.text
            .map { it?.toString().orEmpty().trim() }
            .filter { isValidCandidate(it) }
            .maxByOrNull { scorePlainText(it) }
        if (isValidCandidate(eventBest)) return eventBest

        val source = event.source
        val sourceCandidate = source?.let { bestFromSourceContext(it) }
        if (isValidCandidate(sourceCandidate)) return sourceCandidate

        if (root == null) return null
        return bestFromNodeTree(root, maxNodes = 260)
    }

    private fun bestFromSourceContext(source: AccessibilityNodeInfo): String? {
        val queue: ArrayDeque<AccessibilityNodeInfo> = ArrayDeque()
        val visited = HashSet<Int>()

        var cursor: AccessibilityNodeInfo? = source
        var hops = 0
        while (cursor != null && hops < 5) {
            queue.add(cursor)
            cursor.parent?.let { queue.add(it) }
            cursor = cursor.parent
            hops++
        }

        var best: String? = null
        var bestScore = Int.MIN_VALUE
        var scanned = 0

        while (queue.isNotEmpty() && scanned < 120) {
            val node = queue.removeFirst()
            scanned++

            val key = System.identityHashCode(node)
            if (!visited.add(key)) continue

            val candidates = listOf(
                node.text?.toString(),
                node.contentDescription?.toString()
            )
            for (candidate in candidates) {
                if (!isValidCandidate(candidate)) continue
                val score = scoreCandidate(candidate!!, node)
                if (score > bestScore) {
                    bestScore = score
                    best = candidate
                }
            }

            for (i in 0 until node.childCount) {
                node.getChild(i)?.let(queue::add)
            }
        }

        return best
    }

    private fun bestFromNodeTree(root: AccessibilityNodeInfo, maxNodes: Int): String? {
        val queue: ArrayDeque<AccessibilityNodeInfo> = ArrayDeque()
        queue.add(root)
        var scanned = 0
        var best: String? = null
        var bestScore = Int.MIN_VALUE

        while (queue.isNotEmpty() && scanned < maxNodes) {
            val node = queue.removeFirst()
            scanned++

            val candidates = listOf(
                node.text?.toString(),
                node.contentDescription?.toString()
            )

            for (candidate in candidates) {
                if (!isValidCandidate(candidate)) continue
                val score = scoreCandidate(candidate!!, node)
                if (score > bestScore) {
                    bestScore = score
                    best = candidate
                }
            }

            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }

        return best
    }

    private fun isValidCandidate(text: String?): Boolean {
        if (text.isNullOrBlank()) return false
        val normalized = text.trim()
        if (normalized.length !in MIN_LENGTH..MAX_LENGTH) return false
        if (STOP_WORDS.any { normalized.equals(it, ignoreCase = true) }) return false
        return normalized.any { it.isLetterOrDigit() || isCjk(it) }
    }

    private fun scoreCandidate(text: String, node: AccessibilityNodeInfo): Int {
        var score = scorePlainText(text)
        if (node.isSelected) score += 80
        if (node.isFocused) score += 30
        return score
    }

    private fun scorePlainText(text: String): Int {
        var score = text.length
        if (text.any { it.isDigit() }) score += 12
        if (text.any { isCjk(it) }) score += 12
        if (text.length in 2..80) score += 10
        return score
    }

    private fun isCjk(ch: Char): Boolean {
        return ch.code in 0x4E00..0x9FFF
    }

    companion object {
        private const val MIN_LENGTH = 1
        private const val MAX_LENGTH = 300
        private val STOP_WORDS = setOf(
            "复制", "拷贝", "copy", "剪切", "cut", "粘贴", "paste", "全选", "select all",
            "分享", "share", "删除", "delete", "更多", "more", "提醒"
        )
    }
}
