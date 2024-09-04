package com.example.tourister.models

data class User(
    val username: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val profileImageUrl: String? = null,
    val points: Int = 0 // Add points attribute
)
