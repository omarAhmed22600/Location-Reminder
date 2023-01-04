package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    @Before
    fun setup()
    {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Unconfined
        )
    }
    @After
    fun tearDown()
    {
        database.close()
    }
    @Test
    fun deleteAllReminders() = runBlocking {
        //when deleting all reminders
        val reminder = ReminderDTO("Test Title",
            "Test Description",
            "London",
            10.456465,
            5.321232)
        repository.saveReminder(reminder)

        repository.deleteAllReminders()
        val databaseResult = repository.getReminders()
        assertThat(databaseResult is Result.Success, `is`(true))
        databaseResult as Result.Success
        //then the list is empty and result is success
        assertThat(databaseResult.data , `is`(emptyList()) )
    }
    @Test
    fun getReminders() = runBlocking {
        //when calling getReminders
        val reminder1 = ReminderDTO("Test Title1",
            "Test Description1",
            "London",
            10.456465,
            5.321232)
        val reminder2 = ReminderDTO("Test Title2",
            "Test Description2",
            "Egypt",
            50.456465,
            45.321232)
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)
        val databaseResult = repository.getReminders()
        assertThat(databaseResult is Result.Success, `is`(true))
        databaseResult as Result.Success
        //Then a list of all reminders saved is returned and result is Success
        assertThat(databaseResult.data.size, `is`(CoreMatchers.equalTo(2)))
        assertThat(databaseResult.data[0].title, `is`(CoreMatchers.equalTo(reminder1.title)))
        assertThat(databaseResult.data[1].title, `is`(CoreMatchers.equalTo(reminder2.title)))
    }
    @Test
    fun getReminderByIdAndSaveReminder() = runBlockingTest{
        //When Saving Reminder
        //When getting Id of the reminder
        val reminder = ReminderDTO("Test Title",
            "Test Description",
            "London",
            10.456465,
            5.321232)
        repository.saveReminder(reminder)
        val databaseReminder = repository.getReminder(reminder.id)
        assertThat(databaseReminder is Result.Success, `is`(true))
        databaseReminder as Result.Success
        //Then it is Saved in the database
        //Then it gets the reminder by the id
        assertThat(databaseReminder.data?.title, `is`(reminder.title))
    }
    @Test
    fun getReminderById_dummyId() = runBlockingTest {
        //When Trying to get reminder with incorrect id
        val reminder1 = ReminderDTO("Test Title1",
            "Test Description1",
            "London",
            10.456465,
            5.321232)
        val reminder2 = ReminderDTO("Test Title2",
            "Test Description2",
            "Egypt",
            50.456465,
            45.321232)
        repository.saveReminder(reminder1)
        val databaseResult = repository.getReminder(reminder2.id)
        assertThat(databaseResult is Result.Error, `is`(true))
        databaseResult as Result.Error
        //Then it shouldn't return data but return null with error message "Reminder not found!"
        assertThat(databaseResult.message, `is`("Reminder not found!"))
    }
}