package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billbuddy.R
import com.example.billbuddy.ui.MainViewModel

@Composable
fun ListEventScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    // Warna sesuai desain
    val backgroundColor = Color(0xFFFFDCDC) // Latar pink
    val buttonColor = Color(0xFFFFB6C1) // Warna tombol pink
    val textColor = Color(0xFF4A4A4A) // Warna teks abu-abu tua

    // Definisikan FontFamily untuk font kustom
    val jomhuriaFontFamily = FontFamily(
        Font(R.font.jomhuria_regular)
    )

    // State untuk loading
    val isLoading = remember { mutableStateOf(true) }

    // Ambil daftar semua event dari ViewModel
    val events by viewModel.allEvents.observeAsState(initial = emptyList())
    val error by viewModel.error.observeAsState()

    // Panggil saat layar dimuat
    LaunchedEffect(Unit) {
        viewModel.getAllEvents()
        isLoading.value = false
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("input_event_screen")
                },
                shape = CircleShape,
                containerColor = buttonColor,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Event"
                )
            }
        },
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
                    selected = true,
                    onClick = { /* Sudah di ListEventScreen */ },
                    icon = { Icon(Icons.Filled.List, contentDescription = "List") },
                    label = { Text("List") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("profile_screen") },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hello, Buddy!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { navController.navigate("search_screen") }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Judul Aplikasi
            Text(
                text = "BillBuddy",
                fontSize = 90.sp,
                fontWeight = FontWeight.Bold,
                color = buttonColor,
                fontFamily = jomhuriaFontFamily
            )
            Text(
                text = "IT'S HERE",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = buttonColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Judul Bagian
            Text(
                text = "All Events",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tampilkan loading, error, atau daftar event
            if (isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (error != null) {
                Text(
                    text = "Error: ${error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (events.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(events) { event ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Ikon grup (placeholder)
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.Gray, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "👥", fontSize = 20.sp)
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Nama Event
                                Text(
                                    text = event.eventName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor,
                                    modifier = Modifier.weight(1f)
                                )

                                // Tombol Cek Detail
                                TextButton(
                                    onClick = {
                                        navController.navigate("event_detail_screen/${event.eventId}")
                                    }
                                ) {
                                    Text(
                                        text = "Cek Detail",
                                        fontSize = 14.sp,
                                        color = buttonColor
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Belum ada event",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}