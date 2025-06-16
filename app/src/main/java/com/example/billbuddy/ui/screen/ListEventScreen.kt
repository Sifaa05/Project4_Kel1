package com.example.billbuddy.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.components.*
import com.example.billbuddy.ui.theme.PinkButtonStroke
import com.example.billbuddy.ui.viewModel.MainViewModel
import com.example.billbuddy.ui.components.CommonNavigationBar

//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListEventScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
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
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            HomeHeader(navController = navController, viewModel = viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            // Branding (Horizontal untuk ListEventScreen)
            AppBranding(isHorizontal = true)

            Spacer(modifier = Modifier.height(16.dp))

            // Judul Bagian
            Text(
                text = "All Events",
                style = MaterialTheme.typography.titleLarge,
                color = PinkButtonStroke,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.shadow(elevation = 30.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tampilkan loading, error, atau daftar event
            LoadingErrorHandler(
                isLoading = isLoading.value,
                error = error,
                events = events,
                textColor = MaterialTheme.colorScheme.onBackground,
                onEventClick = { eventId ->
                    navController.navigate(NavRoutes.EventDetail.createRoute(eventId))
                },
                modifier = Modifier.fillMaxWidth(),
                showDetails = true
            )
        }
    }
}