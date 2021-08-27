package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.checkSelfPermissions
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import timber.log.Timber


class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var marker: Marker
    private val requestCode = 1010

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private var permissionsGranted = false

    private val locationManager: LocationManager by lazy {
        requireActivity().getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
    }

    private val locationListener = LocationListener { location ->
        val longitude = location.longitude
        val latitude = location.latitude
        zoomMapToLocation(longitude, latitude)
    }

    private var hasCentered = false

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermissions()

        _viewModel.selectedLocation.observe(viewLifecycleOwner, Observer {
            if (it != null && it) {
                findNavController().popBackStack()
            }
        })

        _viewModel.dialogResult.observe(viewLifecycleOwner, Observer {
            if (it != null && it) {
                _viewModel.onLocationSelected(marker.position)
            }
        })
//        TODO: call this function after the user confirms on the selected location
        binding.saveButton.setOnClickListener {
            saveButtonClick()
        }
        //TODO Observe selectedLocation and open the map with a marker if there was one previously
        return binding.root
    }

    private fun checkPermissions() {
        val missingPermissions = checkSelfPermissions(permissions, context!!)
        if (missingPermissions.isNotEmpty()) {
            requestPermissions(missingPermissions, requestCode)
        } else {
            permissionsGranted = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.requestCode) {
            val missingPermissions = checkSelfPermissions(this.permissions, context!!)
            if (missingPermissions.isNotEmpty()) {
                Timber.e("The following permissions were not granted: ${missingPermissions.contentDeepToString()}")
                Snackbar.make(
                    binding.root,
                    getString(R.string.missing_location_permissions),
                    Snackbar.LENGTH_LONG
                )
                    // Extracted from: https://github.com/udacity/android-kotlin-geo-fences/blob/master/app/src/main/java/com/example/android/treasureHunt/HuntMainActivity.kt#L145
                    .setAction(R.string.settings) {
                        // Displays App settings screen.
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.show()
            } else {
                permissionsGranted = true
                waitForLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun waitForLocation() {
        if (checkSelfPermissions(permissions, context!!).isEmpty()) {
            val location: Location? =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                val longitude: Double = location.longitude
                val latitude: Double = location.latitude
                zoomMapToLocation(longitude, latitude)
            } else {
                val minTime = 5000L
                val minDistance = 50f
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    locationListener
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        waitForLocation()
    }

    @SuppressLint("MissingPermission")
    private fun zoomMapToLocation(longitude: Double, latitude: Double) {
        locationManager.removeUpdates(locationListener)

        if (::map.isInitialized && !hasCentered) { // If map was not initialized, this method will be called again by onMapReady
            map.isMyLocationEnabled = true
            val latLng = LatLng(latitude, longitude)
            val zoomLevel = 15f
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
            hasCentered = true
        }
    }

    private fun saveButtonClick() {
        val editText = EditText(context!!)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        editText.layoutParams = layoutParams
        val dialog = AlertDialog.Builder(context!!, R.style.Theme_AppCompat_DayNight_Dialog_Alert).setTitle(getString(R.string.location_name))
            .setView(editText)
            .setNegativeButton("Cancel") { _, _ ->
                _viewModel.onLocationStr(null) }
            .setPositiveButton("OK") { _, _ ->
                _viewModel.onLocationStr(editText.text.toString())
            }

        dialog.show()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (permissionsGranted) {
            waitForLocation()
        }
        setMapLongClick()
        setMapStyle()
    }

    private fun setMapLongClick() {
        map.setOnMapLongClickListener { latLng ->
            if (::marker.isInitialized) {
                marker.remove()
            }
            val markerOptions = MarkerOptions().title(getString(R.string.content_text))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                .position(latLng).snippet(
                    getString(
                        R.string.lat_long_snippet,
                        latLng.latitude,
                        latLng.longitude
                    )
                )
            marker = map.addMarker(
                markerOptions
            )
            enableSaveButton()
        }
    }

    private fun enableSaveButton() {
        if (!binding.saveButton.isEnabled) {
            binding.saveButton.text = getString(R.string.save)
            binding.saveButton.isEnabled = true
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))
            if (!success) {
                Timber.e("Style parsing failed!!")
            }
        } catch (e: Resources.NotFoundException) {
            Timber.e(e, "Could not find style!")
        }
    }

}
