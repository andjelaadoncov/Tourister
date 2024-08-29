package com.example.tourister.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tourister.models.LocationViewModel

@Composable
fun MainAppScreen(locationViewModel: LocationViewModel = viewModel()) {
    Text(
        text = "Main App Screen",
        fontSize = 24.sp
    )

    // Start location updates
    LaunchedEffect(Unit) {
        locationViewModel.startLocationUpdates()
    }

    val location by locationViewModel.location.observeAsState()
    MapScreen(location = location)
}

@Preview(showBackground = true)
@Composable
fun MainAppScreenPreview() {
    MainAppScreen()
}
