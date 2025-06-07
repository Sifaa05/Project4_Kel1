package com.example.billbuddy.ui.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {
    val startDestination = mutableStateOf("splash_screen")

    fun setStartDestinationToSplash() {
        startDestination.value = "splash_screen"
    }

    fun checkAuthState(user: FirebaseUser?, isOnboardingCompleted: Boolean) {
        startDestination.value = if (!isOnboardingCompleted) {
            "onboarding_satu_screen"
        } else if (user != null) {
            "home_screen"
        } else {
            "authentication_screen"
        }
    }

    fun signOut() {
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
        checkAuthState(null, true) // Asumsikan onboarding tetap selesai setelah logout
    }
}