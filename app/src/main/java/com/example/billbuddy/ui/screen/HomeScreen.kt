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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.MainViewModel
import com.example.billbuddy.ui.components.*
import com.example.billbuddy.ui.theme.Pink40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    // State untuk Bottom Sheet
    val showBottomSheet = remember { mutableStateOf(false) }

    // Ambil daftar event aktif dari ViewModel
    val activeEvents by viewModel.activeEvents.observeAsState(initial = emptyList())
    val error by viewModel.error.observeAsState()

    // Panggil saat layar dimuat
    LaunchedEffect(Unit) {
        viewModel.getActiveEvents()
    }

    Scaffold(
        floatingActionButton = {
            AppFloatingActionButton(
                onClick = { showBottomSheet.value = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        },
        bottomBar = {
            CommonNavigationBar(
                navController = navController,
                selectedScreen = "Home"
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
            HomeHeader(navController = navController)

            Spacer(modifier = Modifier.height(16.dp))

            // Branding
            AppBranding() // Tetap vertikal dan di tengah

            Spacer(modifier = Modifier.height(16.dp))

            // Judul Bagian
            Text(
                text = "Active Events",
                style = MaterialTheme.typography.titleLarge,
                color = Pink40,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.shadow(elevation = 30.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Daftar Event Aktif
            EventList(
                events = activeEvents,
                error = error,
                textColor = MaterialTheme.colorScheme.onBackground,
                buttonColor = MaterialTheme.colorScheme.primary,
                onEventClick = { eventId ->
                    navController.navigate(NavRoutes.EventDetail.createRoute(eventId))
                }
            )
        }
    }

    // Bottom Sheet untuk Input Method
    InputMethodBottomSheet(
        isVisible = showBottomSheet.value,
        onDismiss = { showBottomSheet.value = false },
        navController = navController
    )
}