package com.example.reminderassistant.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.reminderassistant.data.local.ReminderDatabase
import com.example.reminderassistant.data.local.entity.ReminderEntity
import com.example.reminderassistant.data.mapper.ReminderMapper
import com.example.reminderassistant.domain.model.ReminderStatus
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * 集成测试：ReminderRepositoryImpl（使用 Hilt DI）
 * 演示如何使用 Hilt 在 Android 测试中进行依赖注入
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ReminderRepositoryHiltTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var reminderRepository: ReminderRepository

    private lateinit var database: ReminderDatabase

    @Before
    fun setup() {
        hiltRule.inject()

        // 覆盖数据库为内存数据库（在模块中应该做这个配置）
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ReminderDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Test
    fun testCreateReminder() = runBlocking {
        // 安排：创建提醒项
        val reminderItem = com.example.reminderassistant.domain.model.ReminderItem(
            id = 0,
            title = "Test Reminder",
            note = "Test Note",
            reminderTime = System.currentTimeMillis() + 3600000,
            status = ReminderStatus.ACTIVE
        )

        // 行动：通过 Repository 创建
        reminderRepository.createReminder(reminderItem)

        // 断言：验证数据被保存
        val allReminders = reminderRepository.getAllReminders().first()
        assert(allReminders.isNotEmpty())
        assert(allReminders.any { it.title == "Test Reminder" })
    }

    @Test
    fun testUpdateReminderStatus() = runBlocking {
        // 安排：创建并保存提醒
        val reminderItem = com.example.reminderassistant.domain.model.ReminderItem(
            id = 0,
            title = "Test",
            note = "",
            reminderTime = 0,
            status = ReminderStatus.ACTIVE
        )
        reminderRepository.createReminder(reminderItem)

        val allReminders = reminderRepository.getAllReminders().first()
        val createdReminder = allReminders.first()

        // 行动：更新状态
        val completed = createdReminder.copy(status = ReminderStatus.COMPLETED)
        reminderRepository.updateReminder(completed)

        // 断言：验证状态更新
        val updated = reminderRepository.getAllReminders().first()
        assert(updated.first().status == ReminderStatus.COMPLETED)
    }
}
