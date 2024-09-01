package com.example.tourister.models

data class Attraction(
    val name: String = "",
    val description: String = "",
    val photoUrl: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val attractionType: String = "",
    val ticketPrice: String = "",
    val workingHours: String = "",
    val createdAt: Long = System.currentTimeMillis()
)