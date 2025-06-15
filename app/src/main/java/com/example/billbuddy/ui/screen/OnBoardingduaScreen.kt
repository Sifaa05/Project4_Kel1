package com.example.billbuddy.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billbuddy.R
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.components.AppBranding
import com.example.billbuddy.ui.components.AppFilledButton
import com.example.billbuddy.ui.theme.*

@Composable
fun OnboardingDuaScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Branding
            AppBranding(isHorizontal = true)

            // Konten utama
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Gambar
                Image(
                    painter = painterResource(id = R.drawable.onboarding_dua),
                    contentDescription = "Onboarding Characters",
                    modifier = Modifier
                        .size(200.dp)
                        .background(CardBackground, RoundedCornerShape(8.dp))
                        //.shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Kartu konten
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 10.dp, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Transparent & Fair",
                            style = MaterialTheme.typography.displayMedium,
                            color = PinkButtonStroke,
                            fontFamily = KhulaExtrabold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No need to bother recording expenses. Just enter the total cost, BillBuddy will automatically calculate who should pay how much!",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkGreyText,
                            fontFamily = RobotoFontFamily,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        AppFilledButton(
                            onClick = { navController.navigate(NavRoutes.OnboardingTiga.route) },
                            text = "Next",
                            containerColor = PinkButton,
                            textColor = White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            fontSize = 20,
                            cornerRadius = 60.dp,
                            borderWidth = 2.dp,
                            borderColor = PinkButtonStroke
                        )
                    }
                }
            }
        }
    }
}