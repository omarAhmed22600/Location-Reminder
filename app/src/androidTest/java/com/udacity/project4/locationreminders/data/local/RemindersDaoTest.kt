package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
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
    fun getReminderByIdAndSaveReminder() = runBlockingTest{
        //When Saving Reminder
        //When getting Id of the reminder
        val reminder = ReminderDTO("Test Title",
            "Test Description",
            "London",
            10.456465,
            5.321232)
        database.reminderDao().saveReminder(reminder)
        val databaseReminder = database.reminderDao().getReminderById(reminder.id)
        //Then it is Saved in the database
        //Then it gets the reminder by the id
        assertThat(databaseReminder?.title, `is`(reminder.title))
    }
    @Test
    fun deleteAllReminders() = runBlockingTest {
        //when deleting all reminders
        val reminder = ReminderDTO("Test Title",
            "Test Description",
            "London",
            10.456465,
            5.321232)
        database.reminderDao().saveReminder(reminder)
        database.reminderDao().deleteAllReminders()
        val databaseResult = database.reminderDao().getReminders()
        //then the list is empty
        assertThat(databaseResult, `is`(emptyList()) )
    }
    @Test
    fun getReminders() = runBlockingTest {
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
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        val databaseResult = database.reminderDao().getReminders()
        //Then a list of all reminders saved is returned
        assertThat(databaseResult.size, `is`(CoreMatchers.equalTo(2)))
        assertThat(databaseResult[0].title, `is`(CoreMatchers.equalTo(reminder1.title)))
        assertThat(databaseResult[1].title, `is`(CoreMatchers.equalTo(reminder2.title)))
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
        database.reminderDao().saveReminder(reminder1)
        val databaseResult = database.reminderDao().getReminderById(reminder2.id)
        //Then it shouldn't return data but return null
        assertThat(databaseResult, `is`(CoreMatchers.nullValue()))
    }
}