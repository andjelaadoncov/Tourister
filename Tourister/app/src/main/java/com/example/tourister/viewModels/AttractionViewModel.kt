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

    private val db = FirebaseFirestore.getInstance()


    private val _attractions = MutableStateFlow<List<Attraction>>(emptyList())
    val attractions: StateFlow<List<Attraction>> = _attractions

    private val _fullNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val fullNames: StateFlow<Map<String, String>> = _fullNames


    init {
        loadAttractions() //za ucitavanje svih lokacija na pocetku
    }

    // dodavanje atrakcije u kolekciju
    fun addAttraction(
        attraction: Attraction,
        userId: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("attractions")
            .add(attraction)
            .addOnSuccessListener { documentReference ->
                Log.d("SaveAttraction", "Attraction added successfully with ID: ${documentReference.id}")
                awardPointsToUser(userId, 10)
                onSuccess(documentReference.id)  // vraca se id dodate lokacije
            }
            .addOnFailureListener { exception ->
                Log.e("SaveAttraction", "Failed to add attraction", exception)
                onFailure(exception)
            }
    }


    // ucitavanje atrakcija iz kolekcije
    fun loadAttractions() {
        viewModelScope.launch {
            loadAttractionsFromFirebase { loadedAttractions ->
                Log.d("nikola", "" + loadedAttractions.size)
                _attractions.value = loadedAttractions
            }
        }
    }


//    private fun saveAttractionToFirebase(attraction: Attraction, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("attractions")
//            .add(attraction)
//            .addOnSuccessListener {
//                Log.d("SaveAttraction", "Attraction added successfully")
//                onSuccess()
//            }
//            .addOnFailureListener { exception ->
//                Log.e("SaveAttraction", "Failed to add attraction", exception)
//                onFailure(exception)
//            }
//    }

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

    fun addReview(attractionId: String, review: Review) {
        val db = FirebaseFirestore.getInstance()
        val attractionRef = db.collection("attractions").document(attractionId)
        val reviewRef = attractionRef.collection("reviews").document(review.userId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(attractionRef)
            val attraction = snapshot.toObject(Attraction::class.java)

            if (attraction != null) {
                // ako postoji review
                if (transaction.get(reviewRef).exists()) {
                    throw IllegalStateException("You have already submitted a review for this attraction.")
                } else {
                    // ako ne postoji da se doda
                    transaction.set(reviewRef, review)
                    // update nakon dodatog review-a
                    val newNumberOfReviews = attraction.numberOfReviews + 1
                    val newAverageRating = (attraction.averageRating * (attraction.numberOfReviews) + review.rating) / newNumberOfReviews
                    transaction.update(attractionRef, mapOf(
                        "averageRating" to newAverageRating,
                        "numberOfReviews" to newNumberOfReviews
                    ))
                    awardPointsToUser(review.userId, 5)
                }
            }
        }.addOnSuccessListener {
             Log.e("AttractionViewModel", "success.")
        }.addOnFailureListener { e ->
            Log.e("AttractionViewModel", "failure.", e)
        }
    }


    fun loadUserReview(attractionId: String, userId: String, callback: (Review?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val reviewRef = db.collection("attractions").document(attractionId).collection("reviews").document(userId)

        reviewRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val review = documentSnapshot.toObject(Review::class.java)
                callback(review)
            } else {
                callback(null)
            }
        }.addOnFailureListener { e ->
            Log.e("AttractionViewModel", "Failed to load user review.", e)
            callback(null)
        }
    }


    private fun awardPointsToUser(userId: String, points: Int) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentPoints = snapshot.getLong("points") ?: 0
            val newPoints = currentPoints + points
            transaction.update(userRef, "points", newPoints)
        }.addOnSuccessListener {
            Log.d("AwardPoints", "Points awarded successfully")
        }.addOnFailureListener { e ->
            Log.e("AwardPoints", "Failed to award points", e)
        }

    }

    fun fetchUserFullName(userId: String) {
        viewModelScope.launch {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val fullName = document.getString("fullName")
                    if (fullName != null) {
                        _fullNames.value += (userId to fullName)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FetchUserFullName", "Failed to fetch user full name", exception)
                }
        }
    }
}





