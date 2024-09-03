package com.example.tourister.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourister.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class UserListViewModel : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val firestore = FirebaseFirestore.getInstance()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            try {
                val usersSnapshot = firestore.collection("users")
                    .orderBy("points", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()

                val usersList = usersSnapshot.documents.map { document ->
                    val username = document.getString("username") ?: "Unknown"
                    val fullName = document.getString("fullName") ?: "Unknown"
                    val profileImageUrl = document.getString("profileImageUrl")
                    val phoneNumber = document.getString("phoneNumber") ?: "Unknown"
                    val points = document.getLong("points")?.toInt() ?: 0


                    User(
                        username = username,
                        fullName = fullName,
                        phoneNumber = phoneNumber,
                        profileImageUrl = profileImageUrl,
                        points = points
                    )
                }

                _users.value = usersList
                Log.d("UserListViewModel", "Fetched users: $usersList")
            } catch (e: Exception) {
                Log.e("UserListViewModel", "Error fetching users", e)
                _users.value = emptyList()
            }
        }
    }

}
