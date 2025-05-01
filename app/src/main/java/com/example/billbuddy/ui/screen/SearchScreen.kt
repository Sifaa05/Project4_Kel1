package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.MainViewModel
import com.example.billbuddy.ui.components.AppIconButton
import com.example.billbuddy.ui.components.AppTextButton
import com.example.billbuddy.ui.components.CommonNavigationBar

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    // Warna sesuai desain
    val backgroundColor = Color(0xFFFFDCDC) // Latar pink
    val buttonColor = Color(0xFFFFB6C1) // Warna tombol pink
    val textColor = Color(0xFF4A4A4A) // Warna teks abu-abu tua

    // State untuk kolom pencarian
    val searchQuery = remember { mutableStateOf("") }

    // Ambil daftar semua event dari ViewModel
    val events by viewModel.allEvents.observeAsState(initial = emptyList())
    val error by viewModel.error.observeAsState()

    // Panggil saat layar dimuat
    LaunchedEffect(Unit) {
        viewModel.getAllEvents()
    }

    // Filter event berdasarkan kata kunci pencarian
    val filteredEvents = events.filter {
        it.eventName.contains(searchQuery.value, ignoreCase = true)
    }

    Scaffold(
        bottomBar = {
            CommonNavigationBar(
                navController = navController,
                selectedScreen = "List"
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
                AppIconButton(
                    onClick = { navController.popBackStack() },
                    icon = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .background(buttonColor, shape = RoundedCornerShape(50))
                        .size(40.dp)
                )
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

            Spacer(modifier = Modifier.height(16.dp))

            // Kolom Pencarian
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp)),
                placeholder = { Text("Search event...") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = buttonColor,
                    unfocusedIndicatorColor = textColor
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Judul Bagian
            Text(
                text = "Search Results",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tampilkan error, atau daftar hasil pencarian
            if (error != null) {
                Text(
                    text = "Error: ${error}",
                    color = MaterialTheme.colorScheme.error
                )
            } else if (searchQuery.value.isEmpty()) {
                Text(
                    text = "Enter keywords to search for events",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
            } else if (filteredEvents.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredEvents) { event ->
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
                                AppTextButton(
                                    onClick = {
                                        if (event.eventId.isNotEmpty()) {
                                            navController.navigate(NavRoutes.EventDetail.createRoute(event.eventId))
                                        }
                                    },
                                    text = "Check Details",
                                    textColor = buttonColor,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "No events found",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
            }
        }
    }
}