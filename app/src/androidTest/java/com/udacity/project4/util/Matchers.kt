package com.udacity.project4.util

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert

fun compareReminders(reminder: ReminderDTO, dbReminder: ReminderDTO?) {
    MatcherAssert.assertThat(dbReminder as ReminderDTO, CoreMatchers.notNullValue())
    MatcherAssert.assertThat(dbReminder.id, CoreMatchers.`is`(reminder.id))
    MatcherAssert.assertThat(dbReminder.title, CoreMatchers.`is`(reminder.title))
    MatcherAssert.assertThat(dbReminder.description, CoreMatchers.`is`(reminder.description))
    MatcherAssert.assertThat(dbReminder.location, CoreMatchers.`is`(reminder.location))
    MatcherAssert.assertThat(dbReminder.latitude, CoreMatchers.`is`(reminder.latitude))
    MatcherAssert.assertThat(dbReminder.longitude, CoreMatchers.`is`(reminder.longitude))
}