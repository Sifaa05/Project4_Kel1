package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billbuddy.R
import com.example.billbuddy.model.CurrentUser
import com.example.billbuddy.ui.components.AppIconButton
import com.example.billbuddy.ui.components.CommonNavigationBar

@Composable
fun ProfileScreen(navController: NavController) {
    val backgroundColor = Color(0xFFFFDCDC)
    val buttonColor = Color(0xFFFFB6C1)
    val textColor = Color(0xFF4A4A4A)
    val premiumColor = Color(0xFFFFB6C1)

    val user = CurrentUser.user
    var showProfileDialog by remember { mutableStateOf(false) }

    val jomhuriaFontFamily = FontFamily(
        Font(R.font.jomhuria_regular)
    )
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
                    onClick = { /* Notification action */ },
                    icon = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier
                        .background(buttonColor, shape = RoundedCornerShape(50))
                        .size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Profile Picture
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(buttonColor)
                    .clickable { showProfileDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👤", fontSize = 60.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Username
            Text(
                text = user.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email
            Text(
                text = user.email,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Membership status
            if (user.isPremium) {
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
                        contentDescription = "Premium status",
                        tint = premiumColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout button
            Button(
                onClick = {
                    // TODO: Implement logout logic here
                },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Text("Logout", fontSize = 16.sp, color = Color.White)
            }
        }

        // Profile options dialog
        if (showProfileDialog) {
            AlertDialog(
                onDismissRequest = { showProfileDialog = false },
                title = { Text("Profile Options") },
                text = {
                    Column {
                        Button(
                            onClick = {
                                // TODO: Edit profile logic
                                showProfileDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit Profile", color = Color.White)
                        }

                        Button(
                            onClick = {
                                // TODO: Delete profile logic
                                showProfileDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete Profile", color = Color.White)
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showProfileDialog = false }) {
                        Text("Cancel")
                    }
                },
                containerColor = Color.White
            )
        }
    }
}
