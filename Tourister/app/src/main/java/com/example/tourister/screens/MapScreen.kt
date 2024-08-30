package com.example.tourister.screens

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.tourister.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(location: Location?, onBackToMainScreen: () -> Unit) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val googleMapState = remember { mutableStateOf<GoogleMap?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maps") },
                navigationIcon = {
                    IconButton(onClick = onBackToMainScreen) {
                        Icon(painter = painterResource(id = R.drawable.backarrow), contentDescription = "Back to Main Screen", modifier = Modifier.size(32.dp))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
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
                        Text("+", fontWeight = FontWeight.Bold)
                    }
                    Button(onClick = {
                        googleMapState.value?.animateCamera(CameraUpdateFactory.zoomOut())
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff395068),
                        contentColor = Color.White,
                    )) {
                        Text("-", fontWeight = FontWeight.Bold)
                    }
                }
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
