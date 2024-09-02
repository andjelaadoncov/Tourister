package com.example.tourister.models

import com.google.firebase.firestore.DocumentId

data class Attraction(
    @DocumentId
    val id: String? = null,
    val name: String = "",
    val description: String = "",
    val photoUrl: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val attractionType: String = "",
    val ticketPrice: String = "",
    val workingHours: String = "",
    val addedByUserId: String = "",  // New field for the user who added the attraction
    val createdAt: Long = System.currentTimeMillis()
)
