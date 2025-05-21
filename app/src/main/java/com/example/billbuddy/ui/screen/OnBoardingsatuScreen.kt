package com.example.billbuddy.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billbuddy.R
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.theme.BillBuddyTheme

@Composable
fun OnboardingSatuScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    BillBuddyTheme {
        // Warna latar belakang sesuai dengan #FFF7ACB8
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFFFDCDC))
        ) {
            // Gambar karakter (pria dan wanita dalam satu gambar)
            Image(
                painter = painterResource(id = R.drawable.onboarding_satu), // Ganti dengan nama file gambar yang sesuai
                contentDescription = "Onboarding Characters",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp, start = 16.dp, end = 16.dp)
            )

            // Kotak teks
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-50).dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.White.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .padding(64.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = (Color(0xFFFFDCDC)),
                                shape = RoundedCornerShape(32.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Patungan Jadi Gampang!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B1E3F),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Capek hitung uang patungan secara manual? BillBuddy siap bantu kamu bagi biaya dengan mudah dan cepat!",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    // Navigasi ke layar berikutnya (misalnya HomeScreen)
                                    navController.navigate(NavRoutes.OnboardingDua.route)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF48FB1)),
                                shape = RoundedCornerShape(32.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = "Next",
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}