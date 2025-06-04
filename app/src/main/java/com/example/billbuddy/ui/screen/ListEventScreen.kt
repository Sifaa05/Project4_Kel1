package com.example.billbuddy.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.viewModel.MainViewModel
import com.example.billbuddy.ui.components.CommonNavigationBar
import com.example.billbuddy.ui.components.CommonEventCard

// Definisikan FontFamily untuk font kustom
val jomhuriaFontFamily = FontFamily(
    Font(resId = R.font.jomhuria_regular, weight = FontWeight.Normal)
)

@Composable
fun ListEventScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    // Warna sesuai desain
    val backgroundColor = Color(0xFFFFDCDC) // Latar pink
    val buttonColor = Color(0xFFFFB6C1) // Warna tombol pink
    val textColor = Color(0xFF4A4A4A) // Warna teks abu-abu tua

    // State untuk loading
    val isLoading = remember { mutableStateOf(true) }

    // Ambil daftar semua event dari ViewModel
    val events by viewModel.allEvents.observeAsState(initial = emptyList())
    val error by viewModel.error.observeAsState()

    // Panggil saat layar dimuat
    LaunchedEffect(Unit) {
        Log.d("ListEventScreen", "Call getAllEvents()")
        viewModel.getAllEvents()
        isLoading.value = false
    }

    // Logging untuk debugging
    LaunchedEffect(events) {
        Log.d("ListEventScreen", "Number of events accepted: ${events.size}")
        events.forEach { event ->
            Log.d("ListEventScreen", "Event: ${event.eventName}, ID: ${event.eventId}")
        }
    }

    Scaffold(
        bottomBar = {
            CommonNavigationBar(
                navController = navController,
                selectedScreen = "List"
            )
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
                IconButton(onClick = { navController.navigate(NavRoutes.Search.route) }) {
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
            when {
                isLoading.value -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                events.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(events) { event ->
                            CommonEventCard(
                                event = event,
                                textColor = textColor,
                                buttonColor = buttonColor,
                                onClick = {
                                    if (event.eventId.isNotEmpty()) {
                                        navController.navigate(NavRoutes.EventDetail.createRoute(event.eventId))
                                    } else {
                                        Log.e("ListEventScreen", "Invalid eventId for event: ${event.eventName}")
                                    }
                                },
                                showDetails = true
                            )
                        }
                    }
                }
                else -> {
                    Text(
                        text = "No events yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}