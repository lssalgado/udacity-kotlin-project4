package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.checkSelfPermissions
import com.udacity.project4.utils.onPermissionResult
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.setMapStyle
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    companion object {
        @VisibleForTesting
        var dialogEditTextId: Int? = null
    }

    private lateinit var marker: Marker
    private val requestCode = 1010

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )


    private val locationManager: LocationManager by lazy {
        requireActivity().getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
    }

    private val locationListener = LocationListener { location ->
        val longitude = location.longitude
        val latitude = location.latitude
        moveMapToDeviceLocation(longitude, latitude)
    }

    private var hasCentered = false
    private var cachedLatLng: LatLng? = null
    private lateinit var selectedLatLng: LatLng

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by sharedViewModel()
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

        val lat = _viewModel.latitude.value
        val lng = _viewModel.longitude.value
        if (lat != null && lng != null) {
            cachedLatLng = LatLng(lat, lng)
        }

        binding.saveButton.setOnClickListener {
            saveButtonClick()
        }

        return binding.root
    }

    private fun checkPermissions(): Boolean {
        val missingPermissions = checkSelfPermissions(permissions, context!!)
        return if (missingPermissions.isNotEmpty()) {
            requestPermissions(missingPermissions, requestCode)
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.requestCode) {
            onPermissionResult(this.permissions, binding.root) {
                enableMyLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (checkPermissions()) {
            if (::map.isInitialized) {
                map.isMyLocationEnabled = true
                waitForLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun waitForLocation() {
        val location: Location? =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location != null) {
            val longitude: Double = location.longitude
            val latitude: Double = location.latitude
            moveMapToDeviceLocation(longitude, latitude)
        } else {
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_LOW_POWER
            }
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            val settingsClient = LocationServices.getSettingsClient(requireActivity())
            val locationSettingsResponseTask =
                settingsClient.checkLocationSettings(builder.build())
            locationSettingsResponseTask.addOnSuccessListener {
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

    @SuppressLint("MissingPermission")
    private fun moveMapToDeviceLocation(longitude: Double, latitude: Double) {
        locationManager.removeUpdates(locationListener)

        if (::map.isInitialized && !hasCentered) { // If map was not initialized, this method will be called again by onMapReady
            map.isMyLocationEnabled = true
            val latLng = LatLng(latitude, longitude)
            val zoomLevel = 15f
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
            hasCentered = true
        }
    }

    private fun showTitleDialog() {
        val editText = EditText(context!!)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            editText.id = View.generateViewId()
            dialogEditTextId = editText.id
        }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        editText.layoutParams = layoutParams
        val dialogBuilder = AlertDialog.Builder(context!!, R.style.Theme_AppCompat_DayNight_Dialog_Alert).setTitle(getString(R.string.location_name))
            .setView(editText)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("OK") { _, _ ->
                val title = editText.text.toString()
                _viewModel.onLocationStr(title)
                addMarker(selectedLatLng, title)
            }

        dialogBuilder.show()
    }

    private fun saveButtonClick() {
        _viewModel.onLocationSelected(marker.position)
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

        enableMyLocation()

        setMapLongClick()
        map.setMapStyle(context!!)
        setPoiClick()
        cachedLatLng?.let {
            addMarker(it, _viewModel.reminderSelectedLocationStr.value)
        }
    }

    private fun setMapLongClick() {
        map.setOnMapLongClickListener { latLng ->
            selectedLatLng = latLng
            showTitleDialog()
        }
    }

    private fun setPoiClick() {
        map.setOnPoiClickListener { poi ->
            _viewModel.onLocationStr(poi.name)
            addMarker(poi.latLng, poi.name)
        }
    }

    private fun addMarker(latLng: LatLng, title: String?) {
        if (::marker.isInitialized) {
            marker.remove()
        }
        val markerOptions = MarkerOptions()
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
            .position(latLng)
        title?.let {
            markerOptions.title(it)
        }
        marker = map.addMarker(
            markerOptions
        )
        enableSaveButton()
    }

    private fun enableSaveButton() {
        if (!binding.saveButton.isEnabled) {
            binding.saveButton.text = getString(R.string.save)
            binding.saveButton.isEnabled = true
        }
    }

}
