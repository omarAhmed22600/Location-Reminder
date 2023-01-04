package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.google.firebase.FirebaseApp
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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
    var mainCoroutineRule = MainCoroutineRule()
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var fakeRepository: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel

    @Before
    fun setupViewModel() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext);
        fakeRepository = FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeRepository)
    }
    @After
    fun tearDown() {
        stopKoin()
    }
    @Test
    fun saveReminder_loadingShown() = runBlocking {
        //When Saving a Reminder
        mainCoroutineRule.pauseDispatcher()
        val reminder = ReminderDataItem("Test Title",
            "Test Description",
            "London",
            10.456465,
            5.321232)
        viewModel.validateAndSaveReminder(reminder)
        //Then Loading should be shown
        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()
        //Then Disappears
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()

    }
    @Test
    fun validateEnteredData_isTitleEmpty()
    {
        //When Validating a reminder with Empty Title
        val reminder = ReminderDataItem("",
            "Test Description",
            "London",
            10.456465,
            5.321232)
        //Then SnackBar should display Empty Title Error and return false
        assertThat(viewModel.validateEnteredData(reminder)).isFalse()
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_enter_title)
    }
    @Test
    fun validateEnteredData_isLocationEmpty()
    {
        //When Validating a reminder with Empty Location
        val reminder = ReminderDataItem("Test Title",
            "Test Description",
            "",
            10.456465,
            5.321232)
        //Then SnackBar should display Empty Location Error and return false
        assertThat(viewModel.validateEnteredData(reminder)).isFalse()
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_select_location)
    }
    @Test
    fun validateEnteredData_isLocationAndTitleNotEmpty()
    {
        //When Validating a reminder That Contains Title and Location (Not Empty)
        val reminder = ReminderDataItem("Test Title",
            "Test Description",
            "London",
            10.456465,
            5.321232)
        //Then SnackBar shouldn't display any error and validateEnteredData() returns true
        assertThat(viewModel.validateEnteredData(reminder)).isTrue()
    }

}