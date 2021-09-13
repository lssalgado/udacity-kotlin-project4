package com.udacity.project4.locationreminders.savereminder

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun setUp() {
        dataSource = FakeDataSource()

        val reminder1 = ReminderDTO("Title1", "Description1", "Location1", 100.0, 100.0)
        val reminder2 = ReminderDTO("Title2", "Description2", "Location2", 200.0, 200.0)
        val reminder3 = ReminderDTO("Title3", "Description3", "Location3", 300.0, 300.0)
        dataSource.saveReminders(reminder1, reminder2, reminder3)

        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun selectLocation_setsSelectedLocation() {
        viewModel.onLocationSelected(LatLng(0.0, 0.0))

        val value = viewModel.selectedLocation.getOrAwaitValue()
        assertThat(value, (CoreMatchers.not(nullValue())))
    }

    @Test
    fun selectLocationStr_setsSelectedLocationStr() {
        viewModel.onLocationStr("Location Name")

        val value = viewModel.reminderSelectedLocationStr.getOrAwaitValue()
        assertThat(value, (CoreMatchers.not(nullValue())))
    }

    @Test
    fun validateEnteredData_validReminder_returnTrue() {
        val reminder = ReminderDataItem("Title", "Description", "Location", 1.0, 0.0)

        assertThat(viewModel.showSnackBarInt.value, nullValue())

        val result = viewModel.validateEnteredData(reminder)

        assertThat(viewModel.showSnackBarInt.value, nullValue())
        assertThat(result, `is`(true))
    }

    private fun validateEnteredDataActionsAndReturn(reminder: ReminderDataItem, stringId: Int) {
        assertThat(viewModel.showSnackBarInt.value, nullValue())

        val result = viewModel.validateEnteredData(reminder)

        assertThat(viewModel.showSnackBarInt.getOrAwaitValue(), `is`(stringId))
        assertThat(result, `is`(false))
    }

    @Test
    fun validateEnteredData_nullTitle_returnFalse() {
        val reminder = ReminderDataItem(null, "Description", "Location", 1.0, 0.0)

        validateEnteredDataActionsAndReturn(reminder, R.string.err_enter_title)
    }

    @Test
    fun validateEnteredData_emptyTitle_returnFalse() {
        val reminder = ReminderDataItem("", "Description", "Location", 1.0, 0.0)

        validateEnteredDataActionsAndReturn(reminder, R.string.err_enter_title)
    }

    @Test
    fun validateEnteredData_nullLocation_returnFalse() {
        val reminder = ReminderDataItem("Title", "Description", null, 1.0, 0.0)

        validateEnteredDataActionsAndReturn(reminder, R.string.err_select_location)
    }

    @Test
    fun validateEnteredData_emptyLocation_returnFalse() {
        val reminder = ReminderDataItem("Title", "Description", "", 1.0, 0.0)

        validateEnteredDataActionsAndReturn(reminder, R.string.err_select_location)
    }

    @Test
    fun saveReminder_navigationCommandSet() {
        val reminder = ReminderDataItem("Title", "Description", "Location", 1.0, 0.0)

        mainCoroutineRule.pauseDispatcher()
        viewModel.saveReminder(reminder)
        assertThat(viewModel.showLoading.value, `is`(true))

        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(viewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved !"))
        assertThat(
            viewModel.navigationCommand.getOrAwaitValue(),
            instanceOf(NavigationCommand.Back::class.java)
        )
    }
}