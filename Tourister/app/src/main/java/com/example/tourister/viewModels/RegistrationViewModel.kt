package com.example.tourister.viewModels

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RegistrationViewModel : ViewModel() {
    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var fullName by mutableStateOf("")
        private set

    var phoneNumber by mutableStateOf("")
        private set

    var profileImageUri by mutableStateOf<Uri?>(null)
        private set

    var profileBitmap by mutableStateOf<Bitmap?>(null)
        private set

    private val auth = FirebaseAuth.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()

    fun onUsernameChange(newUsername: String) {
        username = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun onFullNameChange(newFullName: String) {
        fullName = newFullName
    }

    fun onPhoneNumberChange(newPhoneNumber: String) {
        phoneNumber = newPhoneNumber
    }

    fun onProfileImageUriChange(newUri: Uri?) {
        profileImageUri = newUri
    }

    fun onProfileBitmapChange(newBitmap: Bitmap?) {
        profileBitmap = newBitmap
    }

    fun registerUser(onSuccess: (Uri?) -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(username, password).await()
                val user = result.user

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .setPhotoUri(profileImageUri)
                    .build()

                user?.updateProfile(profileUpdates)?.await()

                val imageUri = if (profileBitmap != null) {
                    uploadBitmapAsImage(profileBitmap!!, user!!.uid)
                } else {
                    profileImageUri
                }

                uploadProfileImage(imageUri, user!!.uid) { imageUrl ->
                    saveUserDataToFirestore(user.uid, imageUrl, onSuccess, onError)
                }

            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    private fun uploadBitmapAsImage(bitmap: Bitmap, userId: String): Uri? {
        return try {
            // cuvanje bitmape u fajl
            val file = File.createTempFile("profile_image_$userId", ".jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Uri.fromFile(file)
        } catch (e: IOException) {
            Log.e("RegistrationViewModel", "Failed to convert bitmap to file", e)
            null
        }
    }

    private fun uploadProfileImage(profileImageUri: Uri?, userId: String, onComplete: (Uri?) -> Unit) {
        if (profileImageUri == null) {
            Log.d("RegistrationViewModel", "Profile image URI is null")
            onComplete(null)
            return
        }

        Log.d("RegistrationViewModel", "Uploading profile image: $profileImageUri")

        val profileImageRef = storageRef.child("profile_images/$userId.jpg")

        profileImageRef.putFile(profileImageUri)
            .addOnSuccessListener {
                profileImageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Log.d("RegistrationViewModel", "Profile image uploaded successfully: $downloadUri")
                    onComplete(downloadUri)
                }
            }
            .addOnFailureListener {
                Log.e("RegistrationViewModel", "Failed to upload profile image", it)
                onComplete(null)
            }
    }

    private fun saveUserDataToFirestore(
        userId: String,
        profileImageUrl: Uri?,
        onSuccess: (Uri?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val userData = hashMapOf(
            "username" to username,
            "fullName" to fullName,
            "phoneNumber" to phoneNumber,
            "password" to password,
            "profileImageUrl" to profileImageUrl?.toString(),
            "points" to 0
        )

        firestore.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                Log.d("RegistrationViewModel", "User data successfully written!")
                onSuccess(profileImageUrl)
            }
            .addOnFailureListener { e ->
                Log.e("RegistrationViewModel", "Error writing user data", e)
                onError(e)
            }
    }
}
