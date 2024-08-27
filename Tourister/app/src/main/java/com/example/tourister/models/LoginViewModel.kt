package com.example.tourister.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun loginUser(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(username, password).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e) // Trigger error callback
            }
        }
    }
}
