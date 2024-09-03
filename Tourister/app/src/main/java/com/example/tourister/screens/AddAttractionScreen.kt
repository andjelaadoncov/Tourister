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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tourister.models.Attraction
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.storage.FirebaseStorage
import com.example.tourister.viewModels.AttractionViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAttractionScreen(
    latitude: Double,
    longitude: Double,
    currentUserId: String,
    onBackToMapScreen: (LatLng) -> Unit,
    onAddAttraction: (Attraction) -> Unit,
    attractionViewModel: AttractionViewModel
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var attractionType by remember { mutableStateOf("") }
    var ticketPrice by remember { mutableStateOf("") }
    var workingHours by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<String?>(null) }

    // Predefined list of attraction types
    val attractionTypes = listOf(
        "Historical Sites",
        "Natural Attractions",
        "Cultural Attractions",
        "Entertainment Venues",
        "Religious Sites",
        "Architectural Marvels",
        "Adventure Destinations"
    )

    var expanded by remember { mutableStateOf(false) }

    // Create a Uri for storing the captured image
    val photoFile = remember { File(context.cacheDir, "captured_image.jpg") }
    val capturedPhotoUri = remember {
        FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            photoFile
        )
    }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Log.d("AddAttractionScreen", "Image selected: $uri")
            photoUri = it
            uploadImage(it, { url ->
                imageUrl = url
            }, { exception ->
                Log.e("AddAttractionScreen", "Failed to upload image", exception)
            })
        }
    }

    val takePhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            Log.d("AddAttractionScreen", "Image captured: $capturedPhotoUri")
            uploadImage(capturedPhotoUri, { url ->
                imageUrl = url
            }, { exception ->
                Log.e("AddAttractionScreen", "Failed to upload image", exception)
            })
        } else {
            Log.d("AddAttractionScreen", "Failed to capture image")
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
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
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
                    color = Color(0xff395068),
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

                // Attraction Type Dropdown Menu
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = attractionType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Attraction Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        attractionTypes.forEach { type ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    attractionType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
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
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { pickImageLauncher.launch("image/*") }) {
                        Text("Pick Image")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(onClick = { takePhotoLauncher.launch(capturedPhotoUri) }) {
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
                        workingHours = workingHours,
                        addedByUserId = currentUserId
                    )
                    attractionViewModel.addAttraction(
                        attraction,
                        currentUserId,
                        {
                            // On success
                            attractionViewModel.loadAttractions()
                            onBackToMapScreen(LatLng(latitude, longitude))
                        },
                        { exception ->
                            // On failure
                            Log.e("AddAttractionScreen", "Failed to add attraction", exception)
                        }
                    )
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
        Text("No image selected")
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

    val storageRef = FirebaseStorage.getInstance().reference.child("imagesAttractions/${uri.lastPathSegment}")

    storageRef.putFile(uri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                Log.d("UploadImage", "Image uploaded successfully: $downloadUrl")
                onSuccess(downloadUrl.toString())
            }.addOnFailureListener { exception ->
                Log.e("UploadImage", "Failed to get download URL", exception)
                onFailure(exception)
            }
        }.addOnFailureListener { exception ->
            Log.e("UploadImage", "Failed to upload image", exception)
            onFailure(exception)
        }
}





