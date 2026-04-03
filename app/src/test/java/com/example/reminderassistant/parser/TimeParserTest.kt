package com.example.reminderassistant.parser

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * 时间解析器单元测试
 */
class TimeParserTest {
    private lateinit var timeParser: TimeParser
    
    // 固定的测试日期：2024年4月3日，星期三，上午10:00
    private val fixedNow = LocalDateTime.of(2024, 4, 3, 10, 0, 0)

    @Before
    fun setUp() {
        timeParser = TimeParser()
    }

    // ========== 日期解析测试 ==========
    
    @Test
    fun testParseToday() {
        val result = timeParser.parse("今天14点开会", { fixedNow })
        
        assertNotNull(result.timeMillis)
        assertNotNull(result.matchedText)
        assertTrue(result.confidence > 0.5f)
        assertTrue(result.matchedText?.contains("今天") == true)
    }

    @Test
    fun testParseTomorrow() {
        val result = timeParser.parse("明天上午9点", { fixedNow })
        
        assertNotNull(result.timeMillis)
        assertTrue(result.matchedText?.contains("明天") == true)
    }

    @Test
    fun testParseDayAfterTomorrow() {
        val result = timeParser.parse("后天晚上8点", { fixedNow })
        
        assertNotNull(result.timeMillis)
        assertTrue(result.matchedText?.contains("后天") == true)
    }

    @Test
    fun testParseMonthDay() {
        val result = timeParser.parse("5月15日下午3点", { fixedNow })
        
        assertNotNull(result.timeMillis)
        assertTrue(result.confidence > 0.5f)
        assertTrue(result.matchedText?.contains("5月") == true || result.matchedText?.contains("15") == true)
    }

    // ========== 时间解析测试 ==========
    
    @Test
    fun testParseMorningTime() {
        val result = timeParser.parse("明天早上8点", { fixedNow })
        
        assertNotNull(result.timeMillis)
        assertTrue(result.matchedText?.contains("早上") == true || result.matchedText?.contains("8") == true)
    }

    @Test
    fun testParseAfternoonTime() {
        val result = timeParser.parse("下午3点", { fixedNow })
        
        assertNotNull(result.timeMillis)
        assertNotNull(result.matchedText)
    }

    @Test
    fun testParseEveningTime() {
        val result = timeParser.parse("晚上8点半", { fixedNow })
        
        assertNotNull(result.timeMillis)
        assertNotNull(result.matchedText)
    }

    @Test
    fun testParseNoonTime() {
        val result = timeParser.parse("中午12点", { fixedNow })
        
        assertNotNull(result.timeMillis)
        assertNotNull(result.matchedText)
    }

    @Test
    fun testParseTimeWithColon() {
        val result = timeParser.parse("下午14:30开会", { fixedNow })
        
        assertNotNull(result.timeMillis)
    }

    @Test
    fun testParseTimeWithoutPeriod() {
        val result = timeParser.parse("15:30", { fixedNow })
        
        assertNotNull(result.timeMillis)
    }

    // ========== 组合测试 ==========
    
    @Test
    fun testParseDateAndTime() {
        val result = timeParser.parse("5月20日下午3点半", { fixedNow })
        
        assertNotNull(result.timeMillis)
        // 同时识别日期和时间时，置信度应较高
        assertTrue(result.confidence >= 0.7f)
    }

    @Test
    fun testParseOnlyTime() {
        val result = timeParser.parse("下午2点", { fixedNow })
        
        assertNotNull(result.timeMillis)
        // 仅时间，置信度较低
        assertTrue(result.confidence <= 0.6f)
    }

    @Test
    fun testParseOnlyDate() {
        val result = timeParser.parse("明天", { fixedNow })
        
        assertNotNull(result.timeMillis)
    }

    // ========== 边界和异常测试 ==========
    
    @Test
    fun testParseNoDateAndTime() {
        val result = timeParser.parse("这是一段没有时间信息的文本", { fixedNow })
        
        assertNull(result.timeMillis)
        assertNull(result.matchedText)
        assertEquals(0f, result.confidence)
    }

    @Test
    fun testParseEmptyString() {
        val result = timeParser.parse("", { fixedNow })
        
        assertNull(result.timeMillis)
        assertNull(result.matchedText)
        assertEquals(0f, result.confidence)
    }

    @Test
    fun testParseInvalidTime() {
        val result = timeParser.parse("完全没有时间的随机文本", { fixedNow })
        
        // 无有效的时间和日期应该返回空
        assertNull(result.timeMillis)
    }

    // ========== 中文周几测试 ==========
    
    @Test
    fun testParseMonday() {
        val result = timeParser.parse("周一下午2点", { fixedNow })
        
        assertNotNull(result.timeMillis)
        assertTrue(result.matchedText?.contains("周一") == true)
    }

    @Test
    fun testParseFriday() {
        val result = timeParser.parse("周五晚上6点", { fixedNow })
        
        assertNotNull(result.timeMillis)
        assertTrue(result.matchedText?.contains("周五") == true)
    }

    @Test
    fun testParseSunday() {
        val result = timeParser.parse("周日", { fixedNow })
        
        assertNotNull(result.timeMillis)
        assertTrue(result.matchedText?.contains("周日") == true)
    }

    // ========== 复杂文本测试 ==========
    
    @Test
    fun testParseFromRealWorldText1() {
        val text = "明天下午3点到4点开会，地点在会议室"
        val result = timeParser.parse(text, { fixedNow })
        
        assertNotNull(result.timeMillis)
        assertNotNull(result.matchedText)
    }

    @Test
    fun testParseFromRealWorldText2() {
        val text = "5月1号早上8点半打卡，不要迟到"
        val result = timeParser.parse(text, { fixedNow })
        
        assertNotNull(result.timeMillis)
    }

    @Test
    fun testParseFromRealWorldText3() {
        val text = "周五下午提交报告"
        val result = timeParser.parse(text, { fixedNow })
        
        assertNotNull(result.timeMillis)
    }

    // ========== 置信度测试 ==========
    
    @Test
    fun testConfidenceWithBothDateAndTime() {
        val result = timeParser.parse("5月15日下午3点", { fixedNow })
        
        // 同时有日期和时间时，置信度应为0.75
        assertEquals(0.75f, result.confidence)
    }

    @Test
    fun testConfidenceWithOnlyTime() {
        val result = timeParser.parse("下午2点", { fixedNow })
        
        // 仅有时间，置信度 = 0.45 + timeConfidence，应小于0.75
        assertTrue(result.confidence < 0.75f)
        assertTrue(result.confidence > 0.4f)
    }

    // ========== 匹配文本测试 ==========
    
    @Test
    fun testMatchedText() {
        val result = timeParser.parse("明天下午3点半开会", { fixedNow })
        
        assertNotNull(result.matchedText)
        assertTrue(result.matchedText!!.isNotEmpty())
    }

    @Test
    fun testNoMatchedText() {
        val result = timeParser.parse("随便的文本", { fixedNow })
        
        assertNull(result.matchedText)
    }
}
