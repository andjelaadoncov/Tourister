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
import com.example.tourister.R

@Composable
fun AboutScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center // Center content horizontally and vertically
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally, // Center contents horizontally
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "About Tourister",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xff395068), // Adjust color to fit your theme
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = """
                        Tourister is an application that allows users to report and rate tourist attractions. 
                        Users can register their accounts and report tourist attractions by adding basic information 
                        and photos. The app enables users to view a map with marked attractions and filter them by 
                        various attributes. Additionally, it supports rating attractions, adding comments, and ranking 
                        users based on their interactions with attractions. Users also receive notifications about nearby 
                        attractions.
                    """.trimIndent(),
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color.Black
                )

                // Center the image
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .wrapContentSize(align = Alignment.Center)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.attraction), // Replace with your drawable resource
                        contentDescription = "Tourister Logo",
                        modifier = Modifier.size(200.dp) // Adjust size as needed
                    )
                }
            }
        }
    }
}
