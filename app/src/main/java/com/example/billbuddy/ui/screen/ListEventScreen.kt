package com.example.billbuddy.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.components.*
import com.example.billbuddy.ui.theme.DarkGreyText
import com.example.billbuddy.ui.theme.PinkButtonStroke
import com.example.billbuddy.ui.viewModel.MainViewModel
import com.example.billbuddy.ui.viewModel.SortOption

@Composable
fun ListEventScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val isLoading = remember { mutableStateOf(true) }
    val events by viewModel.allEvents.observeAsState(initial = emptyList())
    val error by viewModel.error.observeAsState()
    val sortOption by viewModel.sortOption.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("ListEventScreen", "Call getAllEvents()")
        viewModel.getAllEvents()
        isLoading.value = false
    }

    LaunchedEffect(events) {
        Log.d("ListEventScreen", "Number of events accepted: ${events.size}")
        events.forEach { event ->
            Log.d("ListEventScreen", "Event: ${event.eventName}, ID: ${event.eventId}")
        }
    }

    val sortedEvents by remember(sortOption, events) {
        derivedStateOf {
            when (sortOption) {
                SortOption.NAME_ASC -> events.sortedBy { it.eventName.lowercase() }
                SortOption.NAME_DESC -> events.sortedByDescending { it.eventName.lowercase() }
                SortOption.DATE_ASC -> events.sortedBy { it.timestamp.toDate().time }
                SortOption.DATE_DESC -> events.sortedByDescending { it.timestamp.toDate().time }
            }
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
            HomeHeader(navController = navController, viewModel = viewModel,)
            Spacer(modifier = Modifier.height(16.dp))
            AppBranding(isHorizontal = true)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "All Events",
                    style = MaterialTheme.typography.displayMedium,
                    color = DarkGreyText
                )
                SortButton(
                    currentSortOption = sortOption,
                    onSortSelected = { viewModel.setSortOption(it) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LoadingErrorHandler(
                isLoading = isLoading.value,
                error = error,
                events = sortedEvents,
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