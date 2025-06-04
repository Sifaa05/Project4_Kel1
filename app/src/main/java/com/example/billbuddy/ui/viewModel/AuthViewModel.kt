package com.example.billbuddy.ui.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {
    val startDestination = mutableStateOf("authentication_screen")

    fun checkAuthState(user: FirebaseUser?) {
        startDestination.value = if (user != null) "home_screen" else "authentication_screen"
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        checkAuthState(null )
    }
}