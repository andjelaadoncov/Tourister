package com.example.tourister.models

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
//            val loc = locationResult.lastLocation
//            Log.d("LocationViewModel", "Received location: ${loc?.latitude}, ${loc?.longitude}")
//            _location.value = loc
        }
    }

    private var hasLocation = false

    fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(2000)
            .build()

        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

            // Attempt to get the most accurate current location
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location ->
                if (!hasLocation) {
                    location?.let {
                        _location.value = it
                        hasLocation = true
                    }
                }
            }
        } else {
            Log.d("LocationViewModel", "Location permission not granted!")
        }
    }


    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun setInitialLocation(location: Location?) {
        _location.value = location
    }
}




