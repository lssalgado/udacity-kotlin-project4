package com.udacity.project4.locationreminders

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.checkSelfPermissions
import com.udacity.project4.utils.setMapStyle
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        //        receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }

    private var coroutineJob: Job = Job()
    val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    private var reminder: ReminderDataItem? = null

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private lateinit var binding: ActivityReminderDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val intentReminder = intent.extras?.get(EXTRA_ReminderDataItem)
        if (intentReminder != null && intentReminder is ReminderDataItem) {
            reminder = intentReminder
            binding.reminderDataItem = reminder
        } else {
            handleError()
        }

        binding.backButton.setOnClickListener {
            RemindersActivity.start(this)
        }
    }

    private fun handleError() {

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        Timber.e("onMapReady called!!")
        reminder?.let {
            val lat = it.latitude
            val lng = it.longitude
            if (lat != null && lng != null) {
                val latLng = LatLng(lat, lng)
                val zoomLevel = 15f
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
                if (checkSelfPermissions(permissions, this).isEmpty()) {
                    map.isMyLocationEnabled = true
                }
                map.setMapStyle(this)
            }
        }
    }
}
