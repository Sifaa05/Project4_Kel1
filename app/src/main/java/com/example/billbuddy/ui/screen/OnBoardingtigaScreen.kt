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
import androidx.compose.ui.draw.scale
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
fun OnboardingTigaScreen(
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
                painter = painterResource(id = R.drawable.onboarding_tiga), // Ganti dengan nama file gambar yang sesuai
                contentDescription = "Onboarding Characters",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 150.dp, start = 16.dp, end = 16.dp)
                    .scale(1.5f)
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
                                text = "Nikmati Acara, Bukan Hitungan!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B1E3F),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Fokus nikmati momen, urusan patungan biar BillBuddy yang urus. Mulai sekarang, patungan tanpa drama!",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    // Navigasi ke layar berikutnya (misalnya HomeScreen)
                                    navController.navigate(NavRoutes.Home.route)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFF48FB1
                                    )
                                ),
                                shape = RoundedCornerShape(32.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = "Get Started",
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