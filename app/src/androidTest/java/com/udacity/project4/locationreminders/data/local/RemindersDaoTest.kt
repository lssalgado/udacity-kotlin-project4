package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: RemindersDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        val reminder = ReminderDTO("Title", "Description", "Location", 0.0, 100.0)

        database.reminderDao().saveReminder(reminder)
        val dbReminder = database.reminderDao().getReminderById(reminder.id)


        compareReminders(reminder, dbReminder)
    }

    @Test
    fun insertMultipleRemindersAndGetEachByID() = runBlockingTest {
        val reminders = arrayOf(
            ReminderDTO("Title", "Description", "Location", 1.0, 100.0),
            ReminderDTO("Title2", "Description2", "Location2", 2.0, 200.0),
            ReminderDTO("Title3", "Description3", "Location3", 3.0, 300.0),
            ReminderDTO("Title4", "Description4", "Location4", 4.0, 400.0)
        )

        reminders.forEach { reminder ->
            database.reminderDao().saveReminder(reminder)
        }

        reminders.forEach { reminder ->
            val dbReminder = database.reminderDao().getReminderById(reminder.id)

            compareReminders(reminder, dbReminder)
        }
    }

    @Test
    fun insertMultipleRemindersAndGetAll() = runBlockingTest {
        val reminders = arrayOf(
            ReminderDTO("Title", "Description", "Location", 1.0, 100.0),
            ReminderDTO("Title2", "Description2", "Location2", 2.0, 200.0),
            ReminderDTO("Title3", "Description3", "Location3", 3.0, 300.0),
            ReminderDTO("Title4", "Description4", "Location4", 4.0, 400.0)
        )

        reminders.forEach { reminder ->
            database.reminderDao().saveReminder(reminder)
        }

        val dbReminders = database.reminderDao().getReminders()

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

    private fun compareReminders(reminder: ReminderDTO, dbReminder: ReminderDTO?) {
        assertThat(dbReminder as ReminderDTO, notNullValue())
        assertThat(dbReminder.id, `is`(reminder.id))
        assertThat(dbReminder.title, `is`(reminder.title))
        assertThat(dbReminder.description, `is`(reminder.description))
        assertThat(dbReminder.location, `is`(reminder.location))
        assertThat(dbReminder.latitude, `is`(reminder.latitude))
        assertThat(dbReminder.longitude, `is`(reminder.longitude))
    }
}