package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {

    var testError = false

    fun setThrowError(show : Boolean)
    {
        testError = true
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (testError)
        {
            return Result.Error("Error Retrieving Data!!")
        }
        reminders?.let {
            return Result.Success(it)
        }
        return Result.Error("Reminder data is empty!")

    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val reminder = reminders?.find {
            it.id == id
        }
        if (reminder!=null)
        {
            return Result.Success(reminder)
        }
        return Result.Error("Cannot find this Reminder")
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }


}