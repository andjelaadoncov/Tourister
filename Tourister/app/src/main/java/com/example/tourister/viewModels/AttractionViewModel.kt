package com.example.tourister.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourister.models.Attraction
import com.example.tourister.models.Review
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AttractionViewModel : ViewModel() {

    // MutableStateFlow for storing the list of attractions
    private val _attractions = MutableStateFlow<List<Attraction>>(emptyList())
    val attractions: StateFlow<List<Attraction>> = _attractions

    init {
        loadAttractions() //za ucitavanje svih lokacija na pocetku
    }

    // Function to add an attraction to Firebase and update the state
    fun addAttraction(
        attraction: Attraction,
        onSuccess: (String) -> Unit,  // Return the ID of the saved attraction
        onFailure: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("attractions")
            .add(attraction)
            .addOnSuccessListener { documentReference ->
                Log.d("SaveAttraction", "Attraction added successfully with ID: ${documentReference.id}")
                onSuccess(documentReference.id)  // Pass the ID back
            }
            .addOnFailureListener { exception ->
                Log.e("SaveAttraction", "Failed to add attraction", exception)
                onFailure(exception)
            }
    }


    // Function to load attractions from Firebase
    fun loadAttractions() {
        viewModelScope.launch {
            loadAttractionsFromFirebase { loadedAttractions ->
                Log.d("nikola", "" + loadedAttractions.size)
                _attractions.value = loadedAttractions
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

    fun loadAttractionDetails(attractionId: String, onSuccess: (Attraction?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("attractions").document(attractionId).get()
            .addOnSuccessListener { document ->
                val attraction = document.toObject(Attraction::class.java)
                onSuccess(attraction)
            }
            .addOnFailureListener { exception ->
                onSuccess(null)
            }
    }
     //Function to add a review
     fun addReview(attractionId: String, review: Review) {
         val db = FirebaseFirestore.getInstance()
         val attractionRef = db.collection("attractions").document(attractionId)

         db.runTransaction { transaction ->
             val snapshot = transaction.get(attractionRef)
             val attraction = snapshot.toObject(Attraction::class.java)

             if (attraction != null) {
                 // Ažuriraj prosečnu ocenu i broj recenzija
                 val newNumberOfReviews = attraction.numberOfReviews + 1
                 val totalRating = attraction.averageRating * attraction.numberOfReviews + review.rating
                 val newAverageRating = totalRating / newNumberOfReviews

                 // Ažuriraj atrakciju sa novim vrednostima
                 transaction.update(attractionRef, "averageRating", newAverageRating)
                 transaction.update(attractionRef, "numberOfReviews", newNumberOfReviews)

                 // Dodaj ili ažuriraj recenziju u podkolekciji
                 val reviewRef = attractionRef.collection("reviews").document(review.userId)
                 transaction.set(reviewRef, review)
             }
         }.addOnSuccessListener {
             Log.d("AddReview", "Review added successfully")
         }.addOnFailureListener { exception ->
             Log.e("AddReview", "Failed to add review", exception)
         }
     }




    // Function to load the user's review
    fun loadUserReview(attractionId: String, userId: String, onReviewLoaded: (Review?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val reviewRef = db.collection("attractions").document(attractionId)
            .collection("reviews").document(userId)

        reviewRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val review = documentSnapshot.toObject(Review::class.java)
                onReviewLoaded(review)
            }
            .addOnFailureListener { exception ->
                Log.e("LoadReview", "Failed to load review", exception)
                onReviewLoaded(null)
            }
    }
}





