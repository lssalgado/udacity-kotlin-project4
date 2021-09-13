package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.inject
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : KoinTest {

//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Before and After based on ByungHwa R answer:
    // https://knowledge.udacity.com/questions/635285
    @Before
    fun startKoinForTest() {
        stopKoin()

        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    get(),
                    get() as ReminderDataSource
                )
            }
            val database =
                Room.inMemoryDatabaseBuilder(getApplicationContext(), RemindersDatabase::class.java)
                    .allowMainThreadQueries().build()
            val repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
            single { repository as ReminderDataSource }
            single { LocalDB.createRemindersDao(getApplicationContext()) }

        }

        startKoin {
            androidLogger()
            androidContext(getApplicationContext())
            modules(listOf(myModule))
        }
    }

    @After
    fun stopKoinAfterTest() = stopKoin()

    @Test
    fun clickAddReminderButton_navigateToSaveReminderFragment() {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val mockedNavigation = mock(NavController::class.java)

        scenario.onFragment { Navigation.setViewNavController(it.view!!, mockedNavigation) }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(mockedNavigation).navigate(ReminderListFragmentDirections.toSaveReminder())
    }

    @Test
    fun emptyRepository_showNoData() {
        val repository: ReminderDataSource by inject()
        runBlocking {
            repository.deleteAllReminders()
        }

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun filledRepository_showReminders() {
        val repository: ReminderDataSource by inject()
        val reminders = arrayOf(
            ReminderDTO("Title", "Description", "Location", 1.0, 100.0),
            ReminderDTO("Title2", "Description2", "Location2", 2.0, 200.0),
            ReminderDTO("Title3", "Description3", "Location3", 3.0, 300.0),
            ReminderDTO("Title4", "Description4", "Location4", 4.0, 400.0)
        )
        runBlocking {
            reminders.forEach { reminder ->
                repository.saveReminder(reminder)
            }
        }

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withText("Title")).check(matches(isDisplayed()))
        onView(withText("Description")).check(matches(isDisplayed()))
        onView(withText("Location")).check(matches(isDisplayed()))
        onView(withText("Title2")).check(matches(isDisplayed()))
        onView(withText("Description2")).check(matches(isDisplayed()))
        onView(withText("Location2")).check(matches(isDisplayed()))
        onView(withText("Title3")).check(matches(isDisplayed()))
        onView(withText("Description3")).check(matches(isDisplayed()))
        onView(withText("Location3")).check(matches(isDisplayed()))
        onView(withText("Title4")).check(matches(isDisplayed()))
        onView(withText("Description4")).check(matches(isDisplayed()))
        onView(withText("Location4")).check(matches(isDisplayed()))
    }

    @Test
    fun emptyRepository_showNoData_fillRepository_showReminders() {
        val repository: ReminderDataSource by inject()
        runBlocking {
            repository.deleteAllReminders()
        }

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))

        val reminders = arrayOf(
            ReminderDTO("Title", "Description", "Location", 1.0, 100.0),
            ReminderDTO("Title2", "Description2", "Location2", 2.0, 200.0),
            ReminderDTO("Title3", "Description3", "Location3", 3.0, 300.0),
            ReminderDTO("Title4", "Description4", "Location4", 4.0, 400.0)
        )
        runBlocking {
            reminders.forEach { reminder ->
                repository.saveReminder(reminder)
            }
        }

        onView(withId(R.id.refreshLayout)).perform(swipeDown())

        onView(withText("Title")).check(matches(isDisplayed()))
        onView(withText("Description")).check(matches(isDisplayed()))
        onView(withText("Location")).check(matches(isDisplayed()))
        onView(withText("Title2")).check(matches(isDisplayed()))
        onView(withText("Description2")).check(matches(isDisplayed()))
        onView(withText("Location2")).check(matches(isDisplayed()))
        onView(withText("Title3")).check(matches(isDisplayed()))
        onView(withText("Description3")).check(matches(isDisplayed()))
        onView(withText("Location3")).check(matches(isDisplayed()))
        onView(withText("Title4")).check(matches(isDisplayed()))
        onView(withText("Description4")).check(matches(isDisplayed()))
        onView(withText("Location4")).check(matches(isDisplayed()))
    }
}