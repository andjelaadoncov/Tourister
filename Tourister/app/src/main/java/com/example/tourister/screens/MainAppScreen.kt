package com.example.tourister.screens

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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tourister.R
import com.example.tourister.models.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    locationViewModel: LocationViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }

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
                        shape = RoundedCornerShape(8.dp),  // Zaobljeni uglovi
                        modifier = Modifier.padding(end = 8.dp)  // Razmak sa desne strane
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
                Text(text = "Home Screen", fontSize = 24.sp)
            }
            composable("map") {
                val location by locationViewModel.location.observeAsState()
                MapScreen(
                    location = location,
                    onBackToMainScreen = { navController.navigate("home") }
                )
            }
        }

        // Start location updates
        LaunchedEffect(Unit) {
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
                            modifier = Modifier.size(32.dp).padding(end = 4.dp)
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
                            modifier = Modifier.size(32.dp).padding(end = 4.dp)
                        )
                        Text("Maps", color = Color.White)
                    }
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainAppScreenPreview() {
    MainAppScreen(onLogout = {})
}
