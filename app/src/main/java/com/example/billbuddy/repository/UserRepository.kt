package com.example.billbuddy.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.billbuddy.data.User

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
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
}