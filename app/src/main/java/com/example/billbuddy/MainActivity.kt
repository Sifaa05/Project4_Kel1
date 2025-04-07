package com.example.billbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.billbuddy.data.SplitBillRepository
import com.example.billbuddy.ui.MainViewModel
import com.example.billbuddy.ui.screen.EventDetailScreen
import com.example.billbuddy.ui.screen.InputEventScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = SplitBillRepository()
        val viewModel = MainViewModel()

        setContent {
            MaterialTheme {
                Surface {
                    AppNavigation(
                        repository = repository,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    repository: SplitBillRepository,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "input_event_screen",
        modifier = modifier
    ) {
        composable("input_event_screen") {
            InputEventScreen(
                onBackClick = { /* Bisa ditambahkan untuk keluar dari aplikasi */ },
                onBillCreated = { eventId ->
                    navController.navigate("event_detail_screen/$eventId")
                },
                repository = repository
            )
        }
        composable("event_detail_screen/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(
                eventId = eventId,
                viewModel = viewModel,
                onAddBuddyClick = { /* TODO: Tambahkan aksi untuk Add Buddy */ }
            )
        }
    }
}