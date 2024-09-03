package com.example.tourister.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tourister.R
import com.example.tourister.models.Attraction
import com.example.tourister.viewModels.AttractionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionSummaryScreen(
    attractionId: String,
    attractionViewModel: AttractionViewModel = viewModel(),
    onBack: () -> Unit
) {
    var attraction by remember { mutableStateOf<Attraction?>(null) }

    // Load attraction details when the screen is composed
    LaunchedEffect(attractionId) {
        attractionViewModel.loadAttractionDetails(attractionId) { fetchedAttraction ->
            attraction = fetchedAttraction
        }
    }

    attraction?.let { attraction ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Attraction Summary", fontSize = 24.sp, color = Color(0xff395068)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(id = R.drawable.backarrow),
                                contentDescription = "Back to Home Page",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = attraction.photoUrl,
                    contentDescription = "Attraction Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = attraction.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Type: ")
                        }
                        append(attraction.attractionType)
                    },
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Ticket Price: ")
                        }
                        append(attraction.ticketPrice)
                    },
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Working Hours: ")
                        }
                        append(attraction.workingHours)
                    },
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Average rating: ")
                        }
                        append(attraction.averageRating.toString())
                    },
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }
    }
}
