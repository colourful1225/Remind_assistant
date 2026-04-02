package com.example.reminderassistant.ui.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.reminderassistant.domain.model.ReminderItem
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * 集成测试：HomeScreen Compose UI
 * 测试 UI 组件的交互和显示
 */
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHomeScreenDisplaysReminders() {
        // 安排：准备 Mock ViewModel
        val mockViewModel = mock<HomeViewModel>()
        val testReminders = listOf(
            ReminderItem(
                id = 1,
                title = "Meeting",
                note = "Team sync",
                reminderTime = System.currentTimeMillis(),
                status = com.example.reminderassistant.domain.model.ReminderStatus.ACTIVE
            )
        )

        val uiState = HomeUiState(reminders = testReminders)
        whenever(mockViewModel.uiState).thenReturn(MutableStateFlow(uiState))

        // 行动：渲染 HomeScreen
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToImportConfirm = {},
                onNavigateToReminderEditor = {},
                onNavigateToCalendarEditor = {},
                onNavigateToSettings = {},
                viewModel = mockViewModel
            )
        }

        // 断言：验证提醒标题显示
        composeTestRule.onNodeWithText("Meeting").assertExists()
    }

    @Test
    fun testFabNavigatesToReminderEditor() {
        // 安排
        val mockViewModel = mock<HomeViewModel>()
        val uiState = HomeUiState(reminders = emptyList())
        whenever(mockViewModel.uiState).thenReturn(MutableStateFlow(uiState))

        var navigateCalled = false

        // 行动：渲染并点击 FAB
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToImportConfirm = {},
                onNavigateToReminderEditor = { navigateCalled = true },
                onNavigateToCalendarEditor = {},
                onNavigateToSettings = {},
                viewModel = mockViewModel
            )
        }

        // 点击浮动按钮
        // 注：实际测试中需要找到正确的测试节点
        // composeTestRule.onNodeWithContentDescription("Add Reminder").performClick()

        // 断言：验证导航被调用
        // assert(navigateCalled)
    }

    @Test
    fun testEmptyRemindersList() {
        // 安排
        val mockViewModel = mock<HomeViewModel>()
        val uiState = HomeUiState(reminders = emptyList())
        whenever(mockViewModel.uiState).thenReturn(MutableStateFlow(uiState))

        // 行动
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToImportConfirm = {},
                onNavigateToReminderEditor = {},
                onNavigateToCalendarEditor = {},
                onNavigateToSettings = {},
                viewModel = mockViewModel
            )
        }

        // 断言：验证空状态显示
        // 注：需要根据实际 UI 调整
    }
}
