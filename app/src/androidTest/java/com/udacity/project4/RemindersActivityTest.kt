package com.udacity.project4

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.locationreminders.savereminder.selectreminderlocation.SelectLocationFragment
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    val dataBindingIdlingResource = DataBindingIdlingResource()

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            // Using inMemoryDatabaseBuilder to avoid overwriting the device's data
            val database =
                Room.inMemoryDatabaseBuilder(getApplicationContext(), RemindersDatabase::class.java)
                    .allowMainThreadQueries().build()
            val repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
            single { repository as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @Test
    fun createReminder_saveReminder_listReminder() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        val title = "Title"
        val desc = "Description"
        val location = "Location"

        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(replaceText(title))
        onView(withId(R.id.reminderDescription)).perform(replaceText(desc))

        onView(withId(R.id.selectLocation)).perform(click())

        onView(withText(R.string.please_select_location)).check(matches(isDisplayed()))
        onView(withId(R.id.mapFragment)).check(matches(isDisplayed()))

        onView(withId(R.id.mapFragment)).perform(longClick())

        onView(withText(R.string.location_name)).check(matches(isDisplayed()))
        onView(withId(SelectLocationFragment.dialogEditTextId!!)).perform(replaceText(location))
        onView(withText("OK")).perform(click())

        onView(withText(R.string.save)).check(matches(isDisplayed())).perform(click())

        onView(withText(title)).check(matches(isDisplayed()))
        onView(withText(location)).check(matches(isDisplayed()))

        onView(withId(R.id.saveReminder)).perform(click())

        onView(withId(R.id.addReminderFAB)).check(matches(isDisplayed()))
        onView(withText(title)).check(matches(isDisplayed()))
        onView(withText(location)).check(matches(isDisplayed()))
    }

    @Test
    fun existingReminder_clickReminder_showDescription_navigateBack() {
        val title = "Title"
        val desc = "Description"
        val location = "Location"
        runBlocking {
            repository.saveReminder(ReminderDTO(title, desc, location, 0.0, 100.0))
        }

        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.addReminderFAB)).check(matches(isDisplayed()))
        onView(withText(title)).check(matches(isDisplayed()))
        onView(withText(location)).check(matches(isDisplayed()))

        onView(withText(title)).perform(click())

        onView(withId(R.id.map)).check(matches(isDisplayed()))
        onView(withText(title)).check(matches(isDisplayed()))
        onView(withText(desc)).check(matches(isDisplayed()))
        onView(withId(R.id.backButton)).check(matches(isDisplayed()))
        onView(withId(R.id.backButton)).perform(click())

        onView(withId(R.id.addReminderFAB)).check(matches(isDisplayed()))
        onView(withText(title)).check(matches(isDisplayed()))
        onView(withText(location)).check(matches(isDisplayed()))
    }

    @Test
    fun createReminder_missingTitle_showSnackbar() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        val desc = "Description"
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderDescription)).perform(replaceText(desc))

        onView(withId(R.id.saveReminder)).perform(click())

        onView(withText(R.string.err_enter_title))
    }

    @Test
    fun createReminder_missingLatLng_showSnackbar() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        val title = "Title"
        val desc = "Description"
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(replaceText(title))
        onView(withId(R.id.reminderDescription)).perform(replaceText(desc))

        onView(withId(R.id.saveReminder)).perform(click())

        onView(withText(R.string.err_select_location))
    }

    @Test
    fun createReminder_cancel_showNoData() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        val title = "Title"
        val desc = "Description"
        val location = "Location"

        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(replaceText(title))
        onView(withId(R.id.reminderDescription)).perform(replaceText(desc))

        onView(withId(R.id.selectLocation)).perform(click())

        onView(withText(R.string.please_select_location)).check(matches(isDisplayed()))
        onView(withId(R.id.mapFragment)).check(matches(isDisplayed()))

        onView(withId(R.id.mapFragment)).perform(longClick())

        onView(withText(R.string.location_name)).check(matches(isDisplayed()))
        onView(withId(SelectLocationFragment.dialogEditTextId!!)).perform(replaceText(location))
        onView(withText("OK")).perform(click())

        activityScenario.onActivity { it.onBackPressed() }

        onView(withText(title)).check(matches(isDisplayed()))
        onView(withText(location)).check(matches(isDisplayed()))

        onView(withId(R.id.saveReminder)).check(matches(isDisplayed()))

        activityScenario.onActivity { it.onBackPressed() }

        onView(withId(R.id.addReminderFAB)).check(matches(isDisplayed()))

        onView(withText(title)).check(doesNotExist())
        onView(withText(location)).check(doesNotExist())
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }
}
