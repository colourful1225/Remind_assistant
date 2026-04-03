package com.example.reminderassistant.ui.screen

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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    monitorEnabled: Boolean,
    onMonitorEnabledChange: (Boolean) -> Unit,
    onHideToBackground: () -> Unit,
    onOpenOverlayPermission: () -> Unit,
    onOpenAccessibilitySettings: () -> Unit,
    onOpenAutostartSettings: () -> Unit,
    onOpenBatterySettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "提醒助手",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF151515)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "开启后可在微信和其他应用复制文本时，从底部弹出提醒操作，不需要打开本应用。",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4F4F4F)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "后台监听总开关",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (monitorEnabled) "已开启，服务将常驻后台" else "已关闭，不再监听复制内容",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6D6D6D)
                    )
                }
                Switch(
                    checked = monitorEnabled,
                    onCheckedChange = onMonitorEnabledChange
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onHideToBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("隐藏到后台（无感运行）")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "保活建议",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "为降低被系统回收概率，建议把下面三个权限都打开。",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6D6D6D)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onOpenOverlayPermission) {
                Text("1) 打开悬浮窗权限")
            }
            TextButton(onClick = onOpenAccessibilitySettings) {
                Text("2) 打开无障碍权限（最小监听）")
            }
            TextButton(onClick = onOpenAutostartSettings) {
                Text("3) 打开自启动管理")
            }
            TextButton(onClick = onOpenBatterySettings) {
                Text("4) 加入电池优化白名单")
            }
        }
    }
}
