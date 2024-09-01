package com.example.tourister.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourister.models.Attraction
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AttractionViewModel : ViewModel() {

    // MutableStateFlow for storing the list of attractions
    private val _attractions = MutableStateFlow<List<Attraction>>(emptyList())
    val attractions: StateFlow<List<Attraction>> = _attractions

    // Function to add an attraction to Firebase and update the state
    fun addAttraction(attraction: Attraction, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            saveAttractionToFirebase(attraction, {
                // Refresh the list of attractions after successful addition
                loadAttractions()
                onSuccess()
            }, {
                onFailure(it)
            })
        }
    }

    // Function to load attractions from Firebase
    fun loadAttractions() {
        viewModelScope.launch {
            loadAttractionsFromFirebase { loadedAttractions ->
                _attractions.value = loadedAttractions
            }
        }
    }
}

// Function to save an attraction to Firebase
private fun saveAttractionToFirebase(attraction: Attraction, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("attractions")
        .add(attraction)
        .addOnSuccessListener {
            Log.d("SaveAttraction", "Attraction added successfully")
            onSuccess()
        }
        .addOnFailureListener { exception ->
            Log.e("SaveAttraction", "Failed to add attraction", exception)
            onFailure(exception)
        }
}

// Function to load attractions from Firebase
private fun loadAttractionsFromFirebase(onAttractionsLoaded: (List<Attraction>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("attractions")
        .get()
        .addOnSuccessListener { result ->
            val attractions = result.toObjects(Attraction::class.java)
            onAttractionsLoaded(attractions)
        }
        .addOnFailureListener { exception ->
            Log.e("LoadAttractions", "Failed to load attractions", exception)
        }
}
