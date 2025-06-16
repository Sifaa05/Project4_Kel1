package com.example.billbuddy.ui.screen.authentication

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.billbuddy.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.theme.KadwaFontFamily
import com.example.billbuddy.ui.theme.KhulaExtrabold
import com.example.billbuddy.ui.theme.KhulaRegular
import kotlinx.coroutines.delay

@Composable
fun VerificationScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    email: String
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var isVerified by remember { mutableStateOf(auth.currentUser?.isEmailVerified ?: false) }

    LaunchedEffect(Unit) {
        while (true) {
            auth.currentUser?.reload()
            isVerified = auth.currentUser?.isEmailVerified ?: false
            if (isVerified) {
                navController.navigate("profile_screen") {
                    popUpTo(NavRoutes.Verification.route) { inclusive = true }
                }
                break
            }
            delay(2000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7ACB8)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back Icon",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Email Verification",
                fontFamily = KhulaExtrabold,
                fontSize = 32.sp,
                color = Color(0xFF000000).copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "We have sent a verification link to\\n$email. Please check your inbox (and spam folder) to verify your email.",
                fontFamily = KhulaRegular,
                fontSize = 16.sp,
                color = Color(0xFF000000).copy(alpha = 0.58f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = {
                    auth.currentUser?.reload()
                    isVerified = auth.currentUser?.isEmailVerified ?: false
                    if (isVerified) {
                        Toast.makeText(context, "Email verified successfully", Toast.LENGTH_SHORT).show()
                        navController.navigate("profile_screen") {
                            popUpTo(NavRoutes.Verification.route) { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Email not yet verified. Please check your email.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4BCC5)),
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(2.dp, Color(0xFFF397AE))
            ) {
                Text(
                    text = "Check Verification Status",
                    fontFamily = KadwaFontFamily,
                    fontSize = 14.sp,
                    color = Color(0xFF000000).copy(alpha = 0.6f),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.billbuddy_characters),
                contentDescription = "BillBuddy Characters",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(bottom = 5.dp)
            )
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview
fun VerificationScreenPreview() {
    VerificationScreen(
        navController = rememberNavController(),
        onBackClick = {},
        email = "email@email.com"
    )
}