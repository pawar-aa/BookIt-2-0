package com.example.bookit20

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.Locale


class MainActivity : AppCompatActivity(), PlaceSelectionListener, OnMapReadyCallback, GoogleMap.OnCameraMoveListener  {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var geocoder: Geocoder

    companion object {
        private const val TAG = "MainActivity"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val DEFAULT_ZOOM = 15f
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Places.initialize(applicationContext, getString(R.string.google_maps_key))

        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(this)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        geocoder = Geocoder(this, Locale.getDefault())

        val nNextBtn = findViewById<Button>(R.id.nextBtn)
        nNextBtn.setOnClickListener {
            val intent = Intent(this, DestinationActivity::class.java)
            intent.putExtra("O_LATITUDE", googleMap.cameraPosition.target.latitude)
            intent.putExtra("O_LONGITUDE", googleMap.cameraPosition.target.longitude)
            startActivity(intent)
        }

    }

    override fun onCameraMove() {
        val latLng = googleMap.cameraPosition.target
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0].getAddressLine(0)
                autocompleteFragment.setText(address)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting address: ${e.message}")
        }
    }


    override fun onPlaceSelected(place: Place) {
        Log.i(TAG, "Place: ${place.name}, ${place.id}, ${place.latLng}")
        googleMap.clear()
        val location = place.latLng
        if (location != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM))
        }
    }

    override fun onError(status: com.google.android.gms.common.api.Status) {
        Log.e(TAG, "Error: ${status.statusMessage}")
    }

    override fun onMapReady(map: GoogleMap) {
        val resourceId = R.raw.custom_map_style
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        val styleOptions = MapStyleOptions.loadRawResourceStyle(this, resourceId)
        googleMap.setMapStyle(styleOptions)

        // Set the camera move listener
        googleMap.setOnCameraMoveListener(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        googleMap.isMyLocationEnabled = true
        googleMap.setOnMyLocationButtonClickListener {
            moveCameraToCurrentLocation()
            true
        }

        moveCameraToCurrentLocation()
    }


    private fun moveCameraToCurrentLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
