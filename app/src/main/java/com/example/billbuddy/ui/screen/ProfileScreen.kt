package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billbuddy.ui.components.AppIconButton
import com.example.billbuddy.ui.components.CommonNavigationBar

@Composable
fun ProfileScreen(
    navController: NavController
) {
    // Warna sesuai desain
    val backgroundColor = Color(0xFFFFDCDC) // Latar pink
    val buttonColor = Color(0xFFFFB6C1) // Warna tombol pink
    val textColor = Color(0xFF4A4A4A) // Warna teks abu-abu tua
    val premiumColor = Color(0xFFFFB6C1) // Warna pink untuk status premium

    Scaffold(
        bottomBar = {
            CommonNavigationBar(
                navController = navController,
                selectedScreen = "Profile"
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                AppIconButton(
                    onClick = { /* TODO: Aksi notifikasi */ },
                    icon = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier
                        .background(buttonColor, shape = RoundedCornerShape(50))
                        .size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Foto Profil (Placeholder)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(buttonColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    fontSize = 60.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nama Pengguna
            Text(
                text = "Application Users",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status Keanggotaan
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PREMIUM",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = premiumColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Premium Status",
                    tint = premiumColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}