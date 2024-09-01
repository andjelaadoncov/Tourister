package com.example.tourister.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tourister.R
import com.example.tourister.models.Attraction
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.storage.FirebaseStorage
import coil.compose.AsyncImage

@Composable
fun AddAttractionScreen(
    latitude: Double,
    longitude: Double,
    onAddAttraction: (Attraction) -> Unit,
    onBackToMapScreen: (LatLng) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var attractionType by remember { mutableStateOf("") }
    var ticketPrice by remember { mutableStateOf("") }
    var workingHours by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<String?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Log.d("nikola", uri.toString())
            photoUri = it
        }
    }

    val takePhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            photoUri?.let {
                uploadImage(it, { url ->
                    imageUrl = url
                }, { exception ->
                    // Handle the error (e.g., show a message to the user)
                })
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                ) {
                Text(
                    text = "Enhance the map by adding your own must-see tourist attractions!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal
                    ),
                    color =Color(0xff395068),
                    textAlign = TextAlign.Center
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name Of The Attraction") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Add Description") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = attractionType,
                    onValueChange = { attractionType = it },
                    label = { Text("Attraction Type") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ticketPrice,
                    onValueChange = { ticketPrice = it },
                    label = { Text("Ticket Price") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = workingHours,
                    onValueChange = { workingHours = it },
                    label = { Text("Working Hours") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.SpaceEvenly
                ) {
                    Button(onClick = { pickImageLauncher.launch("image/*") }) {
                        Text("Pick Image")
                    }
                    Spacer(modifier = Modifier.width(4.dp)) // Adjust the width to your preferred space
                    Button(onClick = { photoUri?.let { takePhotoLauncher.launch(it) } }) {
                        Text("Take Photo")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Display the selected image
                DisplayImage(imageUrl)

                Button(onClick = {
                    val attraction = Attraction(
                        name = name,
                        description = description,
                        photoUrl = imageUrl,
                        latitude = latitude,
                        longitude = longitude,
                        attractionType = attractionType,
                        ticketPrice = ticketPrice,
                        workingHours = workingHours
                    )
                    onAddAttraction(attraction)
                    onBackToMapScreen(LatLng(latitude, longitude))
                }) {
                    Text("Add Attraction")
                }
            }
        }
    }
}


@Composable
fun DisplayImage(imageUrl: String?) {
    if (imageUrl.isNullOrBlank()) {
        // Optionally show a placeholder or fallback UI
        Text("$imageUrl")
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
    }
}

fun uploadImage(uri: Uri?, onSuccess: (String?) -> Unit, onFailure: (Exception) -> Unit) {
    if (uri == null) {
        Log.d("UploadImage", "Image URI is null")
        onSuccess(null)
        return
    }

    Log.d("UploadImage", "Uploading image: $uri")

    val storageRef = FirebaseStorage.getInstance().reference.child("imagesAttractions/${uri.lastPathSegment}")

    storageRef.putFile(uri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                Log.d("UploadImage", "Image uploaded successfully: $downloadUrl")
                onSuccess(downloadUrl.toString())
            }
                .addOnFailureListener { exception ->
                    Log.e("UploadImage", "Failed to get download URL", exception)
                    onFailure(exception)
                }
        }
        .addOnFailureListener { exception ->
            Log.e("UploadImage", "Failed to upload image", exception)
            onFailure(exception)
        }
}





