package com.example.tourister

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tourister.screens.LoginScreen
import com.example.tourister.screens.RegistrationScreen
import com.example.tourister.ui.theme.TouristerTheme
import com.example.tourister.screens.MainAppScreen
import com.example.tourister.viewModels.LocationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TouristerTheme {
                Surface {
                    AppContent()
                }
            }
        }
    }
}

@Composable
fun AppContent() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var isRegistering by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val locationViewModel: LocationViewModel = viewModel()

    if (isRegistering) {
        RegistrationScreen(
            onRegisterSuccess = {
                isRegistering = false
                isLoggedIn = true
            },
            onRegisterError = { exception ->
                errorMessage = exception.message
            },
            navigateToLogin = {
                isRegistering = false
            }
        )
    } else if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = {
                isLoggedIn = true
            },
            onLoginError = { exception ->
                errorMessage = exception.message
            },
            navigateToRegistration = {
                isRegistering = true
            }
        )
    } else {
        MainAppScreen(
            locationViewModel = locationViewModel,
            onLogout = {
                isLoggedIn = false
            }
        )
    }

    errorMessage?.let { msg ->
        ErrorMessage(msg) {
            errorMessage = null
        }
    }
}

@Composable
fun ErrorMessage(message: String, onDismiss: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0x99000000)) // Optional: semi-transparent background
    ) {
        Card(
            colors = CardDefaults.cardColors(Color.White),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = message,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Dismiss")
                }
            }
        }
    }
}



