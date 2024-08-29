package com.example.tourister.screens

import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Composable
fun MapScreen(location: Location?) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val googleMapState = remember { mutableStateOf<GoogleMap?>(null) }

    AndroidView({ mapView }) { mapView ->
        mapView.getMapAsync { googleMap ->
            googleMapState.value = googleMap
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Your Location")

                googleMap.clear() // Clear previous markers
                googleMap.addMarker(markerOptions)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }

    // Adding Zoom Buttons
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                googleMapState.value?.animateCamera(CameraUpdateFactory.zoomIn())
            }, colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xff395068),
                contentColor = Color.White
            )) {
                Text("+", fontWeight = FontWeight.Bold )
            }
            Button(onClick = {
                googleMapState.value?.animateCamera(CameraUpdateFactory.zoomOut())
            },colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xff395068),
                contentColor = Color.White,
            )) {
                Text("-", fontWeight = FontWeight.Bold )
            }
        }
    }
}




@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    DisposableEffect(mapView) {
        mapView.onCreate(null)
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDestroy()
        }
    }

    return mapView
}
