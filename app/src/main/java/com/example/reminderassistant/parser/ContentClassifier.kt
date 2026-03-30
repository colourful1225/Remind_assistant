package com.example.reminderassistant.parser

import com.example.reminderassistant.domain.model.ParsedType
import javax.inject.Inject

class ContentClassifier @Inject constructor() {
    fun classify(text: String, hasTime: Boolean): ParsedType {
        if (hasTime) return ParsedType.EVENT

        val eventKeywords = listOf("会议", "面试", "开会", "上课", "讲座", "约", "预约", "报名")
        if (eventKeywords.any { text.contains(it) }) {
            return ParsedType.EVENT
        }

        val todoKeywords = listOf("提醒", "记得", "待办", "截止", "提交", "完成", "联系")
        if (todoKeywords.any { text.contains(it) }) {
            return ParsedType.TODO
        }

        return ParsedType.UNKNOWN
    }
}
