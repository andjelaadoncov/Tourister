package com.example.tourister.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tourister.viewModels.ProfileViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.tourister.R


@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    onBackToHome: () -> Unit // Callback for navigating back to home
) {
    val user by profileViewModel.userData.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        user?.let {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Profile Image
                    it.profileImageUrl?.let { imageUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape) // Make the image round
                                .align(Alignment.CenterHorizontally)
                        )
                    } ?: run {
                        // Placeholder if no image is available
                        Image(
                            painter = painterResource(id = R.drawable.placeholder),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape) // Make the image round
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    // User Details with bold labels
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Username: ")
                            }
                            append(it.username)
                        },
                        fontSize = 20.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Full Name: ")
                            }
                            append(it.fullName)
                        },
                        fontSize = 20.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Phone: ")
                            }
                            append(it.phoneNumber)
                        },
                        fontSize = 20.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    // Display points
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Points: ")
                            }
                            append(it.points.toString())
                        },
                        fontSize = 20.sp,
                        color = Color.Black
                    )

                    // Spacer
                    Spacer(modifier = Modifier.height(24.dp))

                    // Back Button
                    Button(onClick = onBackToHome, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Use the default back arrow icon
                            contentDescription = "Back Arrow",
                            modifier = Modifier.padding(end = 8.dp) // Add some space between the icon and the text
                        )
                        Text(text = "Back to Home")
                    }
                }
            }
        } ?: run {
            Text(text = "Loading user data...", fontSize = 18.sp, color = Color.Gray)
        }
    }
}