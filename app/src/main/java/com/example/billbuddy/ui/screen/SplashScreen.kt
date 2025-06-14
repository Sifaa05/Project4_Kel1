package com.example.billbuddy.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billbuddy.R
import com.example.billbuddy.navigation.NavRoutes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    sharedPreferences: android.content.SharedPreferences,
    auth: FirebaseAuth
) {
    val backgroundColor = Color(0xFFFFFFFF)

    LaunchedEffect(Unit) {
        delay(2000L)
        val isOnboardingCompleted = sharedPreferences.getBoolean("isOnboardingCompleted", false)
        val destination = if (!isOnboardingCompleted) {
            NavRoutes.OnboardingSatu.route
        } else if (auth.currentUser != null) {
            NavRoutes.Home.route
        } else {
            NavRoutes.Authentication.route
        }
        navController.navigate(destination) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logobillbuddy),
            contentDescription = "BillBuddy Logo",
            modifier = Modifier.size(200.dp)
        )
    }
}