package com.example.tourister

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.tourister.databinding.ActivityMapsBinding
import com.example.tourister.viewModels.LocationViewModel

@Deprecated("")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private var initialLocation: LatLng? = null
    private var hasMoved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

        locationViewModel.location.observe(this) { location ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                if (initialLocation == null) {
                    initialLocation = latLng
                    mMap.addMarker(MarkerOptions().position(latLng).title("Your Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                } else if (!hasMoved) {
                    hasMoved = true
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            locationViewModel.startLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.isMyLocationEnabled = true
                    locationViewModel.startLocationUpdates()
                }
            } else {
                Log.e("MapsActivity", "Location permission denied")
            }
        }
    }
}




