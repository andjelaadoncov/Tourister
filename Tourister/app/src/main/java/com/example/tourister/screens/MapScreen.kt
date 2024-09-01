package com.example.tourister.screens

import LocationService
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.tourister.R
import com.example.tourister.viewModels.AttractionViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    location: Location?,
    onBackToMainScreen: () -> Unit,
    onAddAttraction: (LatLng) -> Unit,
    attractionViewModel: AttractionViewModel = viewModel()
) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val googleMapState = remember { mutableStateOf<GoogleMap?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    val attractions by attractionViewModel.attractions.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Maps") },
                    navigationIcon = {
                        IconButton(onClick = onBackToMainScreen) {
                            Icon(
                                painter = painterResource(id = R.drawable.backarrow),
                                contentDescription = "Back to Main Screen",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )
                ServiceControl()
            }
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

                    // Add markers for existing attractions
                    attractions.forEach { attraction ->
                        val latLng = LatLng(attraction.latitude, attraction.longitude)
                        val markerOptions = MarkerOptions()
                            .position(latLng)
                            .title(attraction.name)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

                        googleMap.addMarker(markerOptions)
                    }

                    googleMap.setOnMapClickListener { latLng ->
                        selectedLatLng = latLng
                        showDialog = true
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
                        contentColor = Color.White
                    )) {
                        Text("-", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (showDialog && selectedLatLng != null) {
            AddAttractionDialog(
                onDismiss = { showDialog = false },
                onConfirm = {
                    onAddAttraction(selectedLatLng!!)
                    showDialog = false
                }
            )
        }
    }
}


@Composable
fun AddAttractionDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Attraction") },
        text = { Text("Do you want to add an attraction here?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ServiceControl() {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xff395068)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = {
            val serviceIntent = Intent(context, LocationService::class.java)
            context.startForegroundService(serviceIntent)
        }) {
            Text("Start Service")
        }

        Button(onClick = {
            val serviceIntent = Intent(context, LocationService::class.java)
            context.stopService(serviceIntent)
        }) {
            Text("Stop Service")
        }
    }
}

fun GoogleMap.addAttractionMarker(latLng: LatLng) {
    val markerOptions = MarkerOptions()
        .position(latLng)
        .title("New Attraction")
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
    this.addMarker(markerOptions)
}

