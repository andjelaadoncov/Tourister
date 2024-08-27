package com.example.tourister.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun MainAppScreen() {
    Text(
        text = "Main App Screen",
        fontSize = 24.sp
    )
}

@Preview(showBackground = true)
@Composable
fun MainAppScreenPreview() {
    MainAppScreen()
}
