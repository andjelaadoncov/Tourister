package com.example.tourister.screens

import android.Manifest
import  com.example.tourister.services.LocationService
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.tourister.R
import com.example.tourister.viewModels.AttractionViewModel
import com.example.tourister.viewModels.LocationViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    locationViewModel: LocationViewModel,
    onBackToMainScreen: () -> Unit,
    onAddAttraction: (LatLng) -> Unit,
    attractionViewModel: AttractionViewModel = viewModel(),
    onAttractionClick: (String) -> Unit,  // Add this callback for navigation
) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val googleMapState = remember { mutableStateOf<GoogleMap?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    val attractions by attractionViewModel.attractions.collectAsState()
    var selectedAttractionType by remember { mutableStateOf<String?>(null) }
    var selectedRating by remember { mutableStateOf<Float?>(0f) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedRadius by remember { mutableDoubleStateOf(10.0) } // Default radius is 10 km
    var showDatePicker by remember { mutableStateOf(false) } // State to control DatePickerDialog

    val location by locationViewModel.location.observeAsState()

    val calendar = Calendar.getInstance()
    val userLocation = location?.let { LatLng(it.latitude, it.longitude) }

    // Side-effect to show DatePickerDialog when showDatePicker changes to true
    if (showDatePicker) {
        android.app.DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                calendar.set(year, month, day)
                selectedDate = calendar.timeInMillis
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun updateMarkers(googleMap: GoogleMap,  userLocation: LatLng?) {
        googleMap.clear() // Clear existing markers

        userLocation?.let {
            val locationMarkerOptions = MarkerOptions()
                .position(it)
                .title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            googleMap.addMarker(locationMarkerOptions)
        }

        // Filter attractions based on selected filters and radius
        val filteredAttractions = attractions.filter { attraction ->
            val attractionLocation = Location("").apply {
                latitude = attraction.latitude
                longitude = attraction.longitude
            }

            val userLocationObj = userLocation?.let {
                Location("").apply {
                    latitude = it.latitude
                    longitude = it.longitude
                }
            } ?: Location("").apply {
                latitude = 0.0
                longitude = 0.0
            }

            val distanceInMeters = userLocationObj.distanceTo(attractionLocation)

            //Log.d("HELPER","Attraction: ${attraction.name}, Distance: $distanceInMeters, Radius: ${radiusInKm * 1000}")


            val isWithinRadius = distanceInMeters <= selectedRadius * 1000

            isWithinRadius &&
                    (selectedAttractionType == null || attraction.attractionType == selectedAttractionType) &&
                    (selectedRating == null || attraction.averageRating >= selectedRating!!) &&
                    (selectedDate == null || isSameDay(attraction.createdAt, selectedDate!!))
        }

        // Add markers for filtered attractions
        filteredAttractions.forEach { attraction ->
            val latLng = LatLng(attraction.latitude, attraction.longitude)
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title(attraction.name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

            googleMap.addMarker(markerOptions)
        }

        // Set up listeners for marker clicks
        googleMap.setOnMarkerClickListener { marker ->
            val selectedAttraction = filteredAttractions.find {
                it.latitude == marker.position.latitude && it.longitude == marker.position.longitude
            }
            selectedAttraction?.let {
                onAttractionClick(it.id!!)
            }
            true
        }

        // Set up listeners for map clicks
        googleMap.setOnMapClickListener { latLng ->
            val existingAttraction = filteredAttractions.find {
                it.latitude == latLng.latitude && it.longitude == latLng.longitude
            }
            if (existingAttraction != null) {
                onAttractionClick(existingAttraction.id!!)
            } else {
                selectedLatLng = latLng
                showDialog = true
            }
        }
    }

    fun updateLocation(location: LatLng) {
        googleMapState.value?.let {
            val latLng = LatLng(location.latitude, location.longitude)
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title("Your Location")

            it.clear() // Clear previous markers
            it.addMarker(markerOptions)
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }


    LaunchedEffect(selectedAttractionType, selectedRating, selectedDate, selectedRadius) {
        googleMapState.value?.let { updateMarkers(googleMap = it, userLocation = userLocation) }
    }

    LaunchedEffect(location) {
        // Initial setup for location marker
        location?.let {
            updateLocation(it)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Maps") },
                    navigationIcon = {
                        IconButton(onClick = onBackToMainScreen) {
                            Icon(
                                painter = painterResource(id = R.drawable.backarrow),
                                contentDescription = "Back to Home Page",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )
                ServiceControl()

                var typeExpanded by remember { mutableStateOf(false) }
                var ratingExpanded by remember { mutableStateOf(false) }
                var dateExpanded by remember { mutableStateOf(false) }
                var radiusExpanded by remember { mutableStateOf(false) }

                Column{
                    // Filter by Type
                    Text(
                        text = "Filter by Type",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { typeExpanded = !typeExpanded }
                            .padding(vertical = 8.dp)
                            .background(Color.Transparent)
                            .padding(horizontal = 16.dp)
                    )
                    if (typeExpanded) {
                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = !typeExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedAttractionType ?: "Select Type",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Attraction Type") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                                },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = typeExpanded,
                                onDismissRequest = { typeExpanded = false }
                            ) {
                                val attractionTypes = listOf(
                                    "Historical Sites",
                                    "Natural Attractions",
                                    "Cultural Attractions",
                                    "Entertainment Venues",
                                    "Religious Sites",
                                    "Architectural Marvels",
                                    "Adventure Destinations"
                                )
                                attractionTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = {
                                            selectedAttractionType = type
                                            typeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Filter by Rating
                    Column(modifier = Modifier.background(Color(0xff395068))){
                        Text(
                            text = "Filter by Rating",
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { ratingExpanded = !ratingExpanded }
                                .padding(vertical = 8.dp)
                                .padding(horizontal = 16.dp)
                        )
                    }
                    if (ratingExpanded) {
                        Slider(
                            value = selectedRating ?: 0f,
                            onValueChange = { selectedRating = it },
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xff395068),
                                activeTrackColor = Color(0xff395068),
                                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            valueRange = 0f..5f,
                            steps = 10,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Text(
                            text = "Rating: ${"%.1f".format(selectedRating)} stars",
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .padding(horizontal = 16.dp)
                        )
                    }

                    // Filter by Date
                    Text(
                        text = "Filter by Date",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                            .padding(vertical = 8.dp)
                            .background(Color.Transparent)
                            .padding(horizontal = 16.dp)
                    )
                    if(dateExpanded){
                        Text(
                        text = selectedDate?.let {
                            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            dateFormat.format(it)
                        } ?: "Select Date",
                        modifier = Modifier.padding(16.dp)
                    )
                    }

                    // Filter by Radius
                    Column(modifier = Modifier.background(Color(0xff395068))){
                        Text(
                            text = "Filter by Radius",
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { radiusExpanded = !radiusExpanded }
                                .padding(vertical = 8.dp)
                                .padding(horizontal = 16.dp)
                        )
                    }
                    if(radiusExpanded){
                        Slider(
                            value = selectedRadius.toFloat(),
                            onValueChange = { selectedRadius = it.toDouble() },
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xff395068),
                                activeTrackColor = Color(0xff395068),
                                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            valueRange = 1f..50f, // Radius range from 1 km to 50 km
                            steps = 19,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Text(
                            text = "Radius: ${"%.1f".format(selectedRadius)} km",
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .padding(horizontal = 16.dp)
                        )
                    }
                }

            }

        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // The AndroidView to render the MapView
            AndroidView({ mapView }) { mapView ->
                mapView.getMapAsync { googleMap ->
                    googleMapState.value = googleMap
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        googleMap.isMyLocationEnabled = true
                    }
                    location?.let {
                        updateLocation(it)
                    }

                    updateMarkers(googleMap = googleMap, userLocation)
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

        if (showDialog) {
            location?.let {
            AddAttractionDialog(
                onDismiss = { showDialog = false },
                onConfirm = {
                    onAddAttraction(LatLng(it.latitude, it.longitude))
                    showDialog = false
                }
            )

            }
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
    var isServiceRunning by remember { mutableStateOf(false) }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xff395068)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = {
            val serviceIntent = Intent(context, LocationService::class.java)
            context.startForegroundService(serviceIntent)
            isServiceRunning = true
        },
            enabled = !isServiceRunning // Dugme je onemogućeno kada je servis pokrenut
        ) {
            Text("Start Service")
        }

        Button(onClick = {
            val serviceIntent = Intent(context, LocationService::class.java)
            context.stopService(serviceIntent)
            isServiceRunning = false
        },
        ) {
            Text("Stop Service")
        }
    }
}


fun isSameDay(date1: Long, date2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = date1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}
