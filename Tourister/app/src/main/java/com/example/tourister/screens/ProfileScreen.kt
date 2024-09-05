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
    onBackToHome: () -> Unit
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

                    it.profileImageUrl?.let { imageUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape)
                                .align(Alignment.CenterHorizontally)
                        )
                    } ?: run {

                        Image(
                            painter = painterResource(id = R.drawable.placeholder),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape)
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

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
                                append("Password: ")
                            }
                            append(it.password)
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
                                append("Phone number: ")
                            }
                            append(it.phoneNumber)
                        },
                        fontSize = 20.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))

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


                    Spacer(modifier = Modifier.height(24.dp))


                    Button(onClick = onBackToHome, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Arrow",
                            modifier = Modifier.padding(end = 8.dp)
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