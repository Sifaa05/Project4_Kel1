package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.components.AppBranding
import com.example.billbuddy.ui.components.CommonNavigationBar
import com.example.billbuddy.ui.components.HomeHeader
import com.example.billbuddy.ui.theme.*
import com.example.billbuddy.ui.viewModel.MainViewModel

data class NotificationData(
    val id: String,
    val message: String,
    val date: String
)

@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    // State untuk simulasi data notifikasi
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val notifications by remember {
        mutableStateOf(
            listOf(
                NotificationData("1", "New event added: Team Meeting", "2025-06-14"),
                NotificationData("2", "Bill due: Internet Subscription", "2025-06-13"),
                NotificationData("3", "Event updated: Project Deadline", "2025-06-12")
            )
        )
    }

    // Simulasi pemuatan data
    LaunchedEffect(Unit) {
        // Misalnya, pengambilan data dari ViewModel
        isLoading = false
    }

    Scaffold(
        bottomBar = {
            CommonNavigationBar(
                navController = navController,
                selectedScreen = "Notifications"
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
            HomeHeader(
                navController = navController,
                viewModel = viewModel,
                showBackButton = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Judul Bagian
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.displayLarge,
                color = PinkButtonStroke
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Daftar Notifikasi
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else if (error != null) {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (notifications.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(notifications) { notification ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = PinkButton)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = notification.message,
                                    fontFamily = KadwaFontFamily,
                                    fontSize = 16.sp,
                                    color = DarkGreyText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notification.date,
                                    fontFamily = RobotoFontFamily,
                                    fontSize = 14.sp,
                                    color = DarkGreyText.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "No notifications yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkGreyText,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}