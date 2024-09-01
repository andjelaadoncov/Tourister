package com.example.tourister.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.tourister.R
import com.example.tourister.viewModels.LoginViewModel
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onLoginError: (Exception) -> Unit,
    navigateToRegistration: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Card for Welcome Message and Login Form
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Discover Local Gems, One Click Away!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Login to Your Account",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xff395068),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Username") },
                    value = username,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    onValueChange = { newValue -> username = newValue },
                    keyboardOptions = KeyboardOptions.Default,
                    leadingIcon = {
                        Icon(painter = painterResource(id = R.drawable.account), contentDescription = "", modifier = Modifier.size(32.dp))
                    }
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    value = password,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    onValueChange = { newValue -> password = newValue },
                    keyboardOptions = KeyboardOptions.Default,
                    leadingIcon = {
                        Icon(painter = painterResource(id = R.drawable.password), contentDescription = "", modifier = Modifier.size(32.dp))
                    }
                )

                Button(
                    onClick = {
                        loginViewModel.loginUser(username, password,
                            onSuccess = onLoginSuccess,
                            onError = onLoginError // Handle errors
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff395068),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(text = "Login")
                }

                TextButton(onClick = navigateToRegistration,  modifier = Modifier.padding(top = 16.dp)) {
                    Text(text = "Don't have an account? Register here")
                }
            }
        }
    }
}
