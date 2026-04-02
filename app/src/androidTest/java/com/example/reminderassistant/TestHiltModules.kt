package com.example.reminderassistant

import android.content.Context
import androidx.room.Room
import com.example.reminderassistant.data.local.ReminderDatabase
import com.example.reminderassistant.data.repository.ReminderRepositoryImpl
import com.example.reminderassistant.domain.repository.ReminderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * 测试 Hilt 模块
 * 覆盖生产环境的模块，提供测试版本的依赖
 * 
 * 使用方式：
 * 在测试类中添加 @HiltAndroidTest 注解，
 * 并在 @Before 中调用 hiltRule.inject()
 * 
 * 示例：
 * @RunWith(AndroidJUnit4::class)
 * @HiltAndroidTest
 * class MyTest {
 *     @get:Rule
 *     val hiltRule = HiltAndroidRule(this)
 *     
 *     @Before
 *     fun setup() {
 *         hiltRule.inject()
 *     }
 * }
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]  // 替换原有的 DatabaseModule
)
object TestDatabaseModule {

    /**
     * 提供内存数据库用于测试
     * 这样测试不会影响真实应用数据
     */
    @Provides
    @Singleton
    fun providesReminderDatabase(
        @ApplicationContext context: Context
    ): ReminderDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            ReminderDatabase::class.java
        ).allowMainThreadQueries()  // 允许在主线程查询（仅测试用）
         .build()
    }
}

/**
 * 可选：如果需要更细粒度的控制，可以创建专门的测试 Repository 实现
 * 
 * 注意：这个模块应该在 app/src/androidTest/java/... 或 app/src/test/java/... 目录下
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [com.example.reminderassistant.di.AppModule::class]  // 如果需要替换 AppModule
)
object TestAppModule {

    @Provides
    @Singleton
    fun providesReminderRepository(
        database: ReminderDatabase
    ): ReminderRepository {
        return ReminderRepositoryImpl(database.reminderDao())
    }
}
