package com.example.billbuddy.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.billbuddy.data.User
import java.text.SimpleDateFormat
import java.util.*

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    fun saveUserToFirestore(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        val currentUser = auth.currentUser ?: return
        val user = User(
            userId = currentUser.uid,
            email = currentUser.email ?: "",
            name = currentUser.displayName ?: "Anonymous",
            photoUrl = currentUser.photoUrl?.toString()
        )
        firestore.collection("users")
            .document(currentUser.uid)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun updateUsername(username:String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val currentUser = auth.currentUser ?: run {
            onFailure(Exception("User not logged in"))
            return
        }
        val userId = currentUser.uid
        firestore.collection("users")
            .whereNotEqualTo("userId",userId)
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    firestore.collection("users").document(userId).update("username", username)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onFailure(e) }
                } else {
                    onFailure(Exception("Username already taken"))
                }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun uploadProfilePhoto(uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val currentUser = auth.currentUser ?: run {
            onFailure(Exception("User not logged in"))
            return
        }
        val userId = currentUser.uid
        val storageRef = storage.reference.child("profiles/$userId/profile.jpg")
        val uploadTask = storageRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                val photoUrl = downloadUri.toString()
                firestore.collection("users").document(userId).update("photoUrl", photoUrl)
                    .addOnSuccessListener { onSuccess(photoUrl) }
                    .addOnFailureListener { e -> onFailure(e) }
            }

        } .addOnFailureListener { e -> onFailure(e) }
    }

    fun getUserProfile(onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
        val currentUser = auth.currentUser ?: run {
            onFailure(Exception("User not logged in"))
            return
        }
        firestore.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java) ?: User(
                        userId = currentUser.uid,
                        email = currentUser.email ?: "",
                        name = currentUser.displayName ?: "Anonymous"
                    )
                    onSuccess(user)
                } else {
                    onFailure(Exception("User profile not found"))
                }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun addEventHistory(eventId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val currentUser = auth.currentUser ?: run {
            onFailure(Exception("User not logged in"))
            return
        }
        firestore.collection("users").document(currentUser.uid).update(
            "eventHistory", com.google.firebase.firestore.FieldValue.arrayUnion(eventId)
        ).addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure }
    }
}