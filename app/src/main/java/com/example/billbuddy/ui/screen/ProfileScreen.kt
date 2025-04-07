package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(
    navController: NavController
) {
    // Warna sesuai desain
    val backgroundColor = Color(0xFFFFE6E6) // Latar pink
    val textColor = Color(0xFF4A4A4A) // Warna teks abu-abu tua
    val premiumColor = Color(0xFFFFB6C1) // Warna pink untuk status premium

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("home_screen") },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("list_event_screen") },
                    icon = { Icon(Icons.Filled.List, contentDescription = "List") },
                    label = { Text("List") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Sudah di ProfileScreen */ },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                IconButton(onClick = { /* TODO: Aksi notifikasi */ }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Foto Profil (Placeholder)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFFFFB6C1), CircleShape),
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
                text = "Pengguna Aplikasi",
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
//package com.example.billbuddy.ui.screen
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material.icons.filled.Notifications
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.List
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.billbuddy.ui.MainViewModel
//
//@Composable
//fun ProfileScreen(
//    navController: NavController,
//    viewModel: MainViewModel
//) {
//    // Warna sesuai desain
//    val backgroundColor = Color(0xFFFFE6E6) // Latar pink
//    val textColor = Color(0xFF4A4A4A) // Warna teks abu-abu tua
//    val premiumColor = Color(0xFFFFB6C1) // Warna pink untuk status premium
//
//    // Ambil data profil dari ViewModel
//    val userProfile by viewModel.userProfile.observeAsState()
//    val error by viewModel.error.observeAsState()
//
//    // Panggil saat layar dimuat
//    LaunchedEffect(Unit) {
//        viewModel.getUserProfile()
//    }
//
//    Scaffold(
//        bottomBar = {
//            NavigationBar(
//                containerColor = Color.White
//            ) {
//                NavigationBarItem(
//                    selected = false,
//                    onClick = { navController.navigate("home_screen") },
//                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
//                    label = { Text("Home") }
//                )
//                NavigationBarItem(
//                    selected = false,
//                    onClick = { navController.navigate("list_event_screen") },
//                    icon = { Icon(Icons.Filled.List, contentDescription = "List") },
//                    label = { Text("List") }
//                )
//                NavigationBarItem(
//                    selected = true,
//                    onClick = { /* Sudah di ProfileScreen */ },
//                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
//                    label = { Text("Profile") }
//                )
//            }
//        },
//        modifier = Modifier
//            .fillMaxSize()
//            .background(backgroundColor)
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Header
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Spacer(modifier = Modifier.weight(1f))
//                IconButton(onClick = { /* TODO: Aksi notifikasi */ }) {
//                    Icon(
//                        imageVector = Icons.Default.Notifications,
//                        contentDescription = "Notifications",
//                        tint = textColor
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // Foto Profil (Placeholder)
//            Box(
//                modifier = Modifier
//                    .size(120.dp)
//                    .background(Color(0xFFFFB6C1), CircleShape),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = "👤",
//                    fontSize = 60.sp
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Nama Pengguna
//            Text(
//                text = userProfile?.name ?: "Pengguna Aplikasi",
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                color = textColor
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Status Keanggotaan
//            if (userProfile?.isPremium == true) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "PREMIUM",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Medium,
//                        color = premiumColor
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Icon(
//                        imageVector = Icons.Default.Check,
//                        contentDescription = "Premium Status",
//                        tint = premiumColor,
//                        modifier = Modifier.size(20.dp)
//                    )
//                }
//            } else {
//                Text(
//                    text = "FREE",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Medium,
//                    color = textColor
//                )
//            }
//
//            // Tampilkan error jika ada
//            error?.let {
//                Spacer(modifier = Modifier.height(16.dp))
//                Text(
//                    text = "Error: $it",
//                    color = MaterialTheme.colorScheme.error,
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                )
//            }
//        }
//    }
//}