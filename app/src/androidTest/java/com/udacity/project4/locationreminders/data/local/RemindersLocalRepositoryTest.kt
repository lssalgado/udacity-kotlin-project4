package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.util.compareReminders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        remindersLocalRepository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveReminderAndGetReminder() = runBlocking {
        val reminder = ReminderDTO("Title", "Description", "Location", 0.0, 100.0)
        remindersLocalRepository.saveReminder(reminder)

        val result = remindersLocalRepository.getReminder(reminder.id)

        assertThat(result, instanceOf(Result.Success::class.java))
        result as Result.Success
        compareReminders(reminder, result.data)
    }

    @Test
    fun saveMultipleRemindersAndGetEachByID() = runBlocking {
        val reminders = arrayOf(
            ReminderDTO("Title", "Description", "Location", 1.0, 100.0),
            ReminderDTO("Title2", "Description2", "Location2", 2.0, 200.0),
            ReminderDTO("Title3", "Description3", "Location3", 3.0, 300.0),
            ReminderDTO("Title4", "Description4", "Location4", 4.0, 400.0)
        )

        reminders.forEach { reminder ->
            remindersLocalRepository.saveReminder(reminder)
        }

        reminders.forEach { reminder ->
            val result = remindersLocalRepository.getReminder(reminder.id)

            assertThat(result, instanceOf(Result.Success::class.java))
            result as Result.Success
            compareReminders(reminder, result.data)
        }
    }

    @Test
    fun saveMultipleRemindersAndGetAll() = runBlocking {
        val reminders = arrayOf(
            ReminderDTO("Title", "Description", "Location", 1.0, 100.0),
            ReminderDTO("Title2", "Description2", "Location2", 2.0, 200.0),
            ReminderDTO("Title3", "Description3", "Location3", 3.0, 300.0),
            ReminderDTO("Title4", "Description4", "Location4", 4.0, 400.0)
        )

        reminders.forEach { reminder ->
            remindersLocalRepository.saveReminder(reminder)
        }

        val result = remindersLocalRepository.getReminders()

        assertThat(result, instanceOf(Result.Success::class.java))
        result as Result.Success
        val dbReminders = result.data
        assertThat(dbReminders.size, `is`(reminders.size))
        reminders.forEach { reminder ->
            try {
                val dbReminder = dbReminders.single { it.id == reminder.id }
                compareReminders(reminder, dbReminder)
            } catch (e: IllegalArgumentException) {
                Assert.fail("More than one reminder was found with the given ID!!")
            } catch (e: NoSuchElementException) {
                Assert.fail("No reminder was found with the given ID!!")
            }
        }
    }

    @Test
    fun getReminderWithEmptyDBAndReturnNull() = runBlocking {
        val result = remindersLocalRepository.getReminder("100")

        assertThat(result, instanceOf(Result.Error::class.java))
        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }

    @Test
    fun getRemindersWithEmptyDBAndReturnEmptyList() = runBlocking {
        val result = remindersLocalRepository.getReminders()

        assertThat(result, instanceOf(Result.Success::class.java))
        result as Result.Success
        assertThat(result.data.size, `is`(0))
    }
}