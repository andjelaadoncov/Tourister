package com.example.tourister

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.tourister.screens.RegistrationScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    RegistrationScreen(
                        onRegisterSuccess = { downloadUri ->
                            // Logika kada je registracija uspešna
                        },
                        onRegisterError = { exception ->
                            // Logika kada registracija nije uspešna
                        }
                    )
                }
            }
        }
    }
}