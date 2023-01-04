package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry

import com.google.common.truth.Truth.assertThat
import com.google.firebase.FirebaseApp
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var fakeRepository: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel

    @Before
    fun setupViewModel() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext);
        fakeRepository = FakeDataSource()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeRepository)
    }

    @After
    fun tearDown() {
        stopKoin()
    }
    @Test
    fun loadReminders_loadingShown()
    {
        mainCoroutineRule.pauseDispatcher()
        //When loading reminders
        viewModel.loadReminders()
        //Then Loading is shown
        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()
    }
    @Test
    fun loadReminders_isNotEmpty() = mainCoroutineRule.runBlockingTest{
        // When Add Loading Reminders saved in the repository which isnt empty
        val reminder = ReminderDTO("Test Title",
            "Test Description",
            "London",
            10.456465,
            5.321232)
        fakeRepository.saveReminder(reminder)
        viewModel.loadReminders()
        //Then Reminder list should be updated with the new data and NOT EMPTY
        assertThat(viewModel.remindersList.getOrAwaitValue()).isNotEmpty()
    }
    @Test
    fun loadReminders_errorShown()
    {
        //When There is Error getting data from the data source
        mainCoroutineRule.pauseDispatcher()
        fakeRepository.setThrowError(true)
        viewModel.loadReminders()
        mainCoroutineRule.resumeDispatcher()
        //Then the SnackBar should show Error Message
        assertThat(viewModel.showSnackBar.getOrAwaitValue()).isEqualTo("Error Retrieving Data!!")
    }
}