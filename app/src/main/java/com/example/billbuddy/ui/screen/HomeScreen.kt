package com.example.billbuddy.ui.screen

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
import com.example.billbuddy.ui.viewModel.MainViewModel
import com.example.billbuddy.ui.components.*
import com.example.billbuddy.ui.theme.Pink40
import com.example.billbuddy.ui.components.AppFloatingActionButton
import com.example.billbuddy.ui.components.CommonNavigationBar
import com.google.firebase.auth.FirebaseAuth

//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    // State for Bottom Sheet
    val showBottomSheet = remember { mutableStateOf(false) }

    // Get active events list from ViewModel
    val activeEvents by viewModel.activeEvents.observeAsState(initial = emptyList())
    val monthlyTotals by viewModel.monthlyTotals.observeAsState(initial = emptyMap()) // Observe monthly totals
    val error by viewModel.error.observeAsState()

    // Call when screen loads
    LaunchedEffect(Unit) {
        viewModel.getActiveEvents()
        // Fetch monthly totals when the screen is launched
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            viewModel.getMonthlyTotals()
        }
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
                selectedScreen = "Home" // "Home" is already in English
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
            // Header (Assuming HomeHeader content is internal or doesn't need translation here)
            HomeHeader(navController = navController)

            Spacer(modifier = Modifier.height(16.dp))

            // Branding (Assuming AppBranding content is internal or doesn't need translation here)
            AppBranding()

            Spacer(modifier = Modifier.height(16.dp))

            // Monthly Spending Chart
            MonthlyBarChart(monthlyTotals = monthlyTotals)

            Spacer(modifier = Modifier.height(16.dp))

            // Section Title
            Text(
                text = "Active Events", // English text
                style = MaterialTheme.typography.titleLarge,
                color = Pink40,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.shadow(elevation = 30.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // List of Active Events
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

    // Bottom Sheet for Input Method
    InputMethodBottomSheet( // Assuming InputMethodBottomSheet handles its own internal text translation
        isVisible = showBottomSheet.value,
        onDismiss = { showBottomSheet.value = false },
        navController = navController
    )
}