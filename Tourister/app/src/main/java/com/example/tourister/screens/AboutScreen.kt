package com.example.tourister.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.example.tourister.R

@Composable
fun AboutScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "About Tourister",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xff395068),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = buildAnnotatedString {
                        append("Welcome to ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xff395068))) {
                            append("Tourister")
                        }
                        append(", your ultimate companion for exploring and discovering the world’s most captivating tourist attractions. Our app empowers you to not only explore new destinations but also to contribute to the global travel community by sharing your experiences.\n\n")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xff395068))) {
                            append("Tourister ")
                        }

                        append("lets you explore and contribute to a global travel community by reporting and rating tourist attractions, complete with photos and descriptions. Discover marked attractions on a detailed map, filter by categories, and stay informed with notifications about nearby sites.\n\n")

                        append("At ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xff395068))) {
                            append("Tourister")
                        }
                        append(", we believe that every journey is enriched by the experiences we share. Join us and become a part of a community that thrives on discovery and adventure.")
                    },
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                )

                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .wrapContentSize(align = Alignment.Center)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.attraction),
                        contentDescription = "Tourister Logo",
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
        }
    }
}
