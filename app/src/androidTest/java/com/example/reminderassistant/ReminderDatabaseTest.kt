package com.example.reminderassistant.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.reminderassistant.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 集成测试：ReminderDatabase
 * 运行在 Android 框架上，测试 Room 数据库功能
 */
@RunWith(AndroidJUnit4::class)
class ReminderDatabaseTest {

    private lateinit var database: ReminderDatabase
    private lateinit var reminderDao: ReminderDao

    @Before
    fun setup() {
        // 使用内存数据库进行测试，不会持久化到磁盘
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ReminderDatabase::class.java
        ).allowMainThreadQueries().build()

        reminderDao = database.reminderDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testInsertAndRetrieveReminder() = runBlocking {
        // 安排：创建测试数据
        val reminder = ReminderEntity(
            id = 1,
            title = "Test Reminder",
            note = "Test Note",
            reminderTime = System.currentTimeMillis() + 3600000,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            sourceType = "MANUAL",
            status = "ACTIVE"
        )

        // 行动：插入数据
        reminderDao.insert(reminder)

        // 断言：验证数据被正确插入
        val allReminders = reminderDao.getAll().first()
        assert(allReminders.size == 1)
        assert(allReminders[0].title == "Test Reminder")
    }

    @Test
    fun testUpdateReminder() = runBlocking {
        // 安排：插入初始数据
        val reminder = ReminderEntity(
            id = 1,
            title = "Original Title",
            note = "Original Note",
            reminderTime = System.currentTimeMillis() + 3600000,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            sourceType = "MANUAL",
            status = "ACTIVE"
        )
        reminderDao.insert(reminder)

        // 行动：更新数据
        val updatedReminder = reminder.copy(title = "Updated Title")
        reminderDao.update(updatedReminder)

        // 断言：验证更新成功
        val result = reminderDao.getAll().first()
        assert(result[0].title == "Updated Title")
    }

    @Test
    fun testDeleteReminder() = runBlocking {
        // 安排：插入两条数据
        val reminder1 = ReminderEntity(
            id = 1, title = "Reminder 1", note = "", reminderTime = 0,
            createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(),
            sourceType = "MANUAL", status = "ACTIVE"
        )
        val reminder2 = ReminderEntity(
            id = 2, title = "Reminder 2", note = "", reminderTime = 0,
            createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(),
            sourceType = "MANUAL", status = "ACTIVE"
        )
        reminderDao.insert(reminder1)
        reminderDao.insert(reminder2)

        // 行动：删除一条数据
        reminderDao.delete(reminder1)

        // 断言：验证只剩下一条数据
        val result = reminderDao.getAll().first()
        assert(result.size == 1)
        assert(result[0].id == 2L)
    }

    @Test
    fun testGetActiveReminders() = runBlocking {
        // 安排：插入多种状态的数据
        val active = ReminderEntity(
            id = 1, title = "Active", note = "", reminderTime = 0,
            createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(),
            sourceType = "MANUAL", status = "ACTIVE"
        )
        val completed = ReminderEntity(
            id = 2, title = "Completed", note = "", reminderTime = 0,
            createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(),
            sourceType = "MANUAL", status = "COMPLETED"
        )
        reminderDao.insert(active)
        reminderDao.insert(completed)

        // 行动：获取活跃的提醒
        val result = reminderDao.getByStatus("ACTIVE").first()

        // 断言：只应该返回活跃的提醒
        assert(result.size == 1)
        assert(result[0].title == "Active")
    }
}
