package com.example.billbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.billbuddy.data.SplitBillRepository
import com.example.billbuddy.ui.MainViewModel
import com.example.billbuddy.ui.screen.AddBuddyScreen
import com.example.billbuddy.ui.screen.AssignItemsScreen
import com.example.billbuddy.ui.screen.EventDetailScreen
import com.example.billbuddy.ui.screen.HomeScreen
import com.example.billbuddy.ui.screen.InputEventScreen
import com.example.billbuddy.ui.screen.ListEventScreen
import com.example.billbuddy.ui.screen.ProfileScreen
import com.example.billbuddy.ui.screen.SearchScreen
import com.example.billbuddy.ui.theme.BillBuddyTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val repository = SplitBillRepository()
        val viewModel = MainViewModel()
        setContent {
            BillBuddyTheme {
                Surface {
                    AppNavigation(repository = repository, viewModel = viewModel)
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
        startDestination = "home_screen",
        modifier = modifier
    ) {
        composable("home_screen") {
            HomeScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("list_event_screen") {
            ListEventScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("profile_screen") {
            ProfileScreen(
                navController = navController
            )
        }
//        composable("profile_screen") {
//            ProfileScreen(
//                navController = navController,
//                viewModel = viewModel
//            )
//        }
        composable("search_screen") {
            SearchScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("input_event_screen") {
            InputEventScreen(
                onBackClick = { navController.popBackStack() },
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
                navController = navController // Teruskan NavController
            )
        }
        composable("add_buddy_screen/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            AddBuddyScreen(
                eventId = eventId,
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("assign_items_screen/{eventId}/{selectedFriendsParam}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val selectedFriendsParam = backStackEntry.arguments?.getString("selectedFriendsParam") ?: ""
            AssignItemsScreen(
                eventId = eventId,
                selectedFriendsParam = selectedFriendsParam,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}