package com.example.tourister.models

data class User(
    val username: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String? = null
)
