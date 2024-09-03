package com.example.tourister.viewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.tourister.models.User

class ProfileViewModel : ViewModel() {

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData


    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()


    init {
        loadUserProfile()
    }


    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            firestore.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val userData = documentSnapshot.toObject(User::class.java)
                    _userData.value = userData
                }
                .addOnFailureListener { exception ->
                    // Handle any errors here
                }
        }
    }

}
