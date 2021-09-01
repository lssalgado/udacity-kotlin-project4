package com.udacity.project4.locationreminders.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment.Companion.GEOFENCE_EVENT
import timber.log.Timber

/**
 * Triggered by the Geofence.  Since we can have many Geofences at once, we pull the request
 * ID from the first Geofence, and locate it within the cached data in our Room DB
 *
 * Or users can add the reminders and then close the app, So our app has to run in the background
 * and handle the geofencing in the background.
 * To do that you can use https://developer.android.com/reference/android/support/v4/app/JobIntentService to do that.
 *
 */

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            Timber.e("Broadcast received!!!!!!!!")

            Timber.e("Error code: ${geofencingEvent.errorCode}")

            when (geofencingEvent.geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> Timber.e("Transition = Enter")
                Geofence.GEOFENCE_TRANSITION_DWELL -> Timber.e("Transition = Dwell")
                Geofence.GEOFENCE_TRANSITION_EXIT -> Timber.e("Transition = Exit")
            }

            geofencingEvent.triggeringGeofences.forEach {
                Timber.e("Geofence ID: ${it.requestId}")
            }

            Timber.e(" Latitude: ${ geofencingEvent.triggeringLocation.latitude }")
            Timber.e(" Longitude: ${ geofencingEvent.triggeringLocation.longitude }")

            GeofenceTransitionsJobIntentService.enqueueWork(context, intent)
        }
//TODO: implement the onReceive method to receive the geofencing events at the background

    }
}