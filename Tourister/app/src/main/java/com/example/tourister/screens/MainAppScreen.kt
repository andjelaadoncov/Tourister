package com.example.tourister.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tourister.R
import com.example.tourister.viewModels.LocationViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainAppScreen(
    locationViewModel: LocationViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val attractionLocations = remember { mutableStateListOf<LatLng>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tourister", fontSize = 24.sp, color = Color(0xff395068)) },
                actions = {
                    Button(
                        onClick = { showLogoutDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xff395068)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Logout", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier.fillMaxWidth(), containerColor = Color(0xff395068) ) {
                NavigationBar(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
               AboutScreen()
            }
            composable("map") {
                val location by locationViewModel.location.observeAsState()
                MapScreen(
                    location = location,
                    onBackToMainScreen = { navController.navigate("home") },
                    onAddAttraction = { latLng ->
                        navController.navigate("addAttraction/${latLng.latitude}/${latLng.longitude}")
                    },
                    attractionLocations = attractionLocations
                )
            }
            composable("profile") {
                Text(text = "Home Screen", fontSize = 24.sp)
            }
            composable("addAttraction/{latitude}/{longitude}") { backStackEntry ->
                val latitude = backStackEntry.arguments?.getString("latitude")?.toDouble() ?: 0.0
                val longitude = backStackEntry.arguments?.getString("longitude")?.toDouble() ?: 0.0
                AddAttractionScreen(
                    latitude = latitude,
                    longitude = longitude,
                    onAddAttraction = { attraction ->
                        // Save to Firebase or local database
                        scope.launch {
                            // Add the attraction location to the list and navigate back to the map
                            attractionLocations.add(LatLng(attraction.latitude, attraction.longitude))
                            navController.navigate("map")
                        }
                    },
                    onBackToMapScreen = { latLng ->
                        scope.launch {
                            attractionLocations.add(latLng)
                            navController.navigate("map")
                        }
                    }
                )
            }
        }

        LaunchedEffect(Unit) {
            Log.d("nikola", "LaunchedEffect")
            locationViewModel.startLocationUpdates()
        }

        // Logout confirmation dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Logout") },
                text = { Text("Do you want to log out for sure?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationBar(navController: NavHostController) {
    NavigationBar(
        containerColor = Color(0xff395068),
        modifier = Modifier.fillMaxWidth()  // Ovaj modifikator osigurava da NavigationBar popuni celu širinu
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),  // Osigurava da Row popuni celu širinu
            horizontalArrangement = Arrangement.SpaceAround  // Ravnomerno rasporedi stavke
        ) {
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate("home") },
                icon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Home",
                            tint = Color.White,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 4.dp)
                        )
                        Text("Home", color = Color.White)
                    }
                }
            )
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate("map") },
                icon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.placeholder),
                            contentDescription = "Maps",
                            tint = Color.White,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 4.dp)
                        )
                        Text("Maps", color = Color.White)
                    }
                }
            )
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate("profile") },
                icon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.account),
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 4.dp)
                        )
                        Text("Profile", color = Color.White)
                    }
                }
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun MainAppScreenPreview() {
    MainAppScreen(onLogout = {})
}
