package com.example.tourister.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tourister.R
import com.example.tourister.viewModels.RegistrationViewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.TextStyle

@Composable
fun RegistrationScreen(
    registrationViewModel: RegistrationViewModel = viewModel(),
    onRegisterSuccess: (Uri?) -> Unit,
    onRegisterError: (Exception) -> Unit,
    navigateToLogin: () -> Unit
) {
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        registrationViewModel.onProfileImageUriChange(uri)
    }

    val username = registrationViewModel.username
    val password = registrationViewModel.password
    val fullName = registrationViewModel.fullName
    val phoneNumber = registrationViewModel.phoneNumber

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Card for Registration Form
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
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
                    text = "Create an Account",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Normal
                    ),
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
                    keyboardOptions = KeyboardOptions.Default,
                    onValueChange = registrationViewModel::onUsernameChange,
                    leadingIcon = {
                        Icon(painter = painterResource(id = R.drawable.account), contentDescription = "", modifier = Modifier.size(32.dp))
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Full name") },
                    value = fullName,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    keyboardOptions = KeyboardOptions.Default,
                    onValueChange = registrationViewModel::onFullNameChange,
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
                    keyboardOptions = KeyboardOptions.Default,
                    onValueChange = registrationViewModel::onPasswordChange,
                    leadingIcon = {
                        Icon(painter = painterResource(id = R.drawable.password), contentDescription = "", modifier = Modifier.size(32.dp))
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Phone Number") },
                    value = phoneNumber,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    keyboardOptions = KeyboardOptions.Default,
                    onValueChange = registrationViewModel::onPhoneNumberChange,
                    leadingIcon = {
                        Icon(painter = painterResource(id = R.drawable.telephone), contentDescription = "", modifier = Modifier.size(26.dp))
                    }
                )

                Button(
                    onClick = {
                        // Otvorite galeriju za izbor slike
                        pickImageLauncher.launch("image/*")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff395068),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(text = "Select Profile Image")
                }

                Button(
                    onClick = {
                        registrationViewModel.registerUser(
                            onSuccess = { uri -> onRegisterSuccess(uri) },
                            onError = onRegisterError // Handle errors
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff395068),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(text = "Register")
                }

                TextButton(onClick = navigateToLogin, modifier = Modifier.padding(top = 16.dp)) {
                    Text(text = "Already have an account? Login here")
                }
            }
        }
    }
}
