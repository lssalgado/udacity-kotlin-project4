package com.udacity.project4.locationreminders.reminderslist

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun setUp() {
        dataSource = FakeDataSource()
        val reminder1 = ReminderDTO("Title1", "Description1", "Location1", 100.0, 100.0)
        val reminder2 = ReminderDTO("Title2", "Description2", "Location2", 200.0, 200.0)
        val reminder3 = ReminderDTO("Title3", "Description3", "Location3", 300.0, 300.0)
        dataSource.saveReminders(reminder1, reminder2, reminder3)

        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun loadReminders_loading() {
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()

        Assert.assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )

        mainCoroutineRule.resumeDispatcher()
        Assert.assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
    }

    @Test
    fun loadReminders_showNoData() {
        dataSource.isToReturnError()

        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()

        Assert.assertThat(
            viewModel.showNoData.value,
            CoreMatchers.nullValue() // Initial value
        )

        mainCoroutineRule.resumeDispatcher()
        Assert.assertThat(
            viewModel.showNoData.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
    }

    @Test
    fun loadReminders_reminders() {
        viewModel.loadReminders()

        val remindersList = viewModel.remindersList.getOrAwaitValue()
        Assert.assertThat(remindersList, CoreMatchers.notNullValue())
        Assert.assertThat(remindersList.size, CoreMatchers.`is`(3))
    }

    @Test
    fun loadReminders_showSnackBar() {
        dataSource.isToReturnError()

        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()

        Assert.assertThat(
            viewModel.showSnackBar.value,
            CoreMatchers.nullValue() // Initial value
        )

        mainCoroutineRule.resumeDispatcher()
        Assert.assertThat(
            viewModel.showSnackBar.getOrAwaitValue().length,
            CoreMatchers.not(0)
        )
    }

}