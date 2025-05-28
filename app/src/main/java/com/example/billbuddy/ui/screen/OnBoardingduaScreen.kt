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
fun OnboardingDuaScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    BillBuddyTheme {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFFFDCDC))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Gambar
                Image(
                    painter = painterResource(id = R.drawable.onboarding_dua),
                    contentDescription = "Onboarding Characters",
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Kotak putih
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.White.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .padding(vertical = 24.dp, horizontal = 16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Transparent & Fair",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B1E3F),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No need to bother recording expenses. Just enter the total cost, BillBuddy will automatically calculate who should pay how much!",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                navController.navigate(NavRoutes.OnboardingTiga.route)
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
