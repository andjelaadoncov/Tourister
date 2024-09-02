package com.example.tourister.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tourister.models.Attraction
import com.example.tourister.models.Review
import com.example.tourister.viewModels.AttractionViewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun AttractionDetailScreen(
    attractionId: String,  // ID of the attraction
    currentUserId: String,  // ID of the current user
    attractionViewModel: AttractionViewModel = viewModel()  // ViewModel instance
) {
    // State variables to hold attraction details and user inputs
    var attraction by remember { mutableStateOf<Attraction?>(null) }
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(0f) }
    var userReview by remember { mutableStateOf<Review?>(null) }

    // Load attraction details when the screen is composed
    LaunchedEffect(attractionId) {
        attractionViewModel.loadAttractionDetails(attractionId) { fetchedAttraction ->
            attraction = fetchedAttraction
        }

        // Load the user's existing review, if any
        attractionViewModel.loadUserReview(attractionId, currentUserId) { existingReview ->
            if (existingReview != null) {
                reviewText = existingReview.comment
                rating = existingReview.rating
                userReview = existingReview
            }
            else {
                rating = 0f // Ensure rating starts at 0 if no review exists
            }
        }
    }

    // Composable layout
    Card(
        modifier = Modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            attraction?.let {

                val painter = rememberAsyncImagePainter(model = it.photoUrl)
                Image(
                    painter = painter,
                    contentDescription = "Attraction Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = it.name,
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal
                    ),
                    color = Color(0xff395068),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Description: ")
                        }
                        append(it.description)
                    },
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Attraction Type: ")
                        }
                        append(it.attractionType)
                    },
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Ticket Price: ")
                        }
                        append(it.ticketPrice)
                    },
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Working Hours: ")
                        }
                        append(it.workingHours)
                    },
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Leave a Review for ${attraction?.name ?: ""}",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal
                ),
                color = Color(0xff395068),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Rating")
            StarRatingBar(
                rating = rating,
                onRatingChanged = { newRating ->
                    rating = newRating.toFloat()
                },
                starCount = 5,
                starSize = 32,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                label = { Text("Your Comment") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val review = Review(
                        userId = currentUserId,
                        rating = rating,
                        comment = reviewText
                    )
                    attractionViewModel.addReview(attractionId, review)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff395068),
                    contentColor = Color.White
                ),
            ) {
                Text(text = "Submit Review")
            }

            Spacer(modifier = Modifier.height(16.dp))

            userReview?.let {
                Text(text = "Your Previous Review", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Rating: ${it.rating}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Comment: ${it.comment}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun StarRatingBar(
    rating: Float,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    starCount: Int = 5,
    starSize: Int = 32,
    starColor: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..starCount) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                modifier = Modifier
                    .size(starSize.dp)
                    .clickable { onRatingChanged(i) },
                tint = starColor
            )
        }
    }
}

