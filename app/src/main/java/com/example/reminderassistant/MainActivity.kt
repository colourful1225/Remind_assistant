package com.example.reminderassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reminderassistant.parser.TimeParser
import com.example.reminderassistant.system.clipboard.ClipboardMonitor
import com.example.reminderassistant.system.intent.XiaomiIntentCaller
import com.example.reminderassistant.ui.components.CategoryChoiceBottomSheet
import com.example.reminderassistant.ui.components.TimeExtractionCard
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var clipboardMonitor: ClipboardMonitor

    @Inject
    lateinit var timeParser: TimeParser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    SimpleHomeScreen(
                        clipboardMonitor = clipboardMonitor,
                        timeParser = timeParser,
                        xiaomiIntentCaller = XiaomiIntentCaller(this)
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 在 Compose 中启动监听
        // 这里通过 LaunchedEffect 处理
    }

    override fun onPause() {
        super.onPause()
        clipboardMonitor.stopMonitoring()
    }
}

@Composable
fun SimpleHomeScreen(
    clipboardMonitor: ClipboardMonitor,
    timeParser: TimeParser,
    xiaomiIntentCaller: XiaomiIntentCaller
) {
    val showChoiceSheet = remember { mutableStateOf(false) }
    val extractedText = remember { mutableStateOf("") }
    val extractedTime = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        clipboardMonitor.startMonitoring { copiedText ->
            // 检查文本是否包含时间信息
            val parseResult = timeParser.parse(copiedText)
            if (parseResult.timeMillis != null && parseResult.confidence > 0.4f) {
                extractedText.value = copiedText
                extractedTime.value = xiaomiIntentCaller.formatTimestamp(parseResult.timeMillis)
                showChoiceSheet.value = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 应用标题
            Text(
                text = "提醒助手",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "复制文本即可自动识别时间",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 使用说明
            InstructionCard(
                step = "1",
                title = "复制文本",
                description = "复制包含时间的文本内容"
            )

            Spacer(modifier = Modifier.height(12.dp))

            InstructionCard(
                step = "2",
                title = "自动识别",
                description = "应用自动提取时间信息"
            )

            Spacer(modifier = Modifier.height(12.dp))

            InstructionCard(
                step = "3",
                title = "选择位置",
                description = "选择保存到日历或备忘录"
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 显示提取的时间卡片（如果有）
            if (extractedText.value.isNotEmpty()) {
                TimeExtractionCard(
                    extractedText = extractedText.value,
                    extractedTime = extractedTime.value,
                    onClose = {
                        extractedText.value = ""
                        extractedTime.value = ""
                        showChoiceSheet.value = false
                    }
                )
            }
        }

        // 选择弹窗
        if (showChoiceSheet.value) {
            androidx.compose.material3.ModalBottomSheet(
                onDismissRequest = {
                    showChoiceSheet.value = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                CategoryChoiceBottomSheet(
                    onCalendarClick = {
                        xiaomiIntentCaller.startMiCalendar(
                            title = extractedText.value,
                            timeMillis = timeParser.parse(extractedText.value).timeMillis,
                            description = extractedText.value
                        )
                        showChoiceSheet.value = false
                    },
                    onNotesClick = {
                        xiaomiIntentCaller.startMiNotes(
                            title = extractedTime.value,
                            content = extractedText.value
                        )
                        showChoiceSheet.value = false
                    },
                    onDismiss = {
                        showChoiceSheet.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun InstructionCard(step: String, title: String, description: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF2196F3))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = step,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
