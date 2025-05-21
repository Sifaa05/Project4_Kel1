package com.example.billbuddy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.billbuddy.data.SplitBillRepository
import com.example.billbuddy.ui.MainViewModel
import com.example.billbuddy.ui.screen.AddBuddyScreen
import com.example.billbuddy.ui.screen.AssignItemsScreen
import com.example.billbuddy.ui.screen.EventDetailScreen
import com.example.billbuddy.ui.screen.HomeScreen
import com.example.billbuddy.ui.screen.InputEventScreen
import com.example.billbuddy.ui.screen.ListEventScreen
import com.example.billbuddy.ui.screen.OnboardingDuaScreen
import com.example.billbuddy.ui.screen.OnboardingSatuScreen
import com.example.billbuddy.ui.screen.OnboardingTigaScreen
import com.example.billbuddy.ui.screen.ParticipantBillDetailScreen
import com.example.billbuddy.ui.screen.ParticipantScreen
import com.example.billbuddy.ui.screen.ProfileScreen
import com.example.billbuddy.ui.screen.SearchScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    repository: SplitBillRepository,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.OnboardingSatu.route,
        modifier = modifier
    ) {
        composable(NavRoutes.OnboardingSatu.route) {
            OnboardingSatuScreen(navController = navController)
        }
        composable(NavRoutes.OnboardingDua.route) {
            OnboardingDuaScreen(navController = navController)
        }
        composable(NavRoutes.OnboardingTiga.route) {
            OnboardingTigaScreen(navController = navController)
        }
        composable(NavRoutes.Home.route) {
            HomeScreen(navController = navController, viewModel = viewModel)
        }
        composable(NavRoutes.ListEvent.route) {
            ListEventScreen(navController = navController, viewModel = viewModel)
        }
        composable(NavRoutes.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(NavRoutes.Search.route) {
            SearchScreen(navController = navController, viewModel = viewModel)
        }
        composable(NavRoutes.InputEvent.route) {
            InputEventScreen(
                navController = navController,
                repository = repository
            )
        }
        composable(NavRoutes.EventDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(
                eventId = eventId,
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(NavRoutes.AddBuddy.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            AddBuddyScreen(
                eventId = eventId,
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(NavRoutes.AssignItems.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val selectedFriendsParam = backStackEntry.arguments?.getString("selectedFriendsParam") ?: ""
            AssignItemsScreen(
                eventId = eventId,
                selectedFriendsParam = selectedFriendsParam,
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(NavRoutes.Participant.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            ParticipantScreen(
                eventId = eventId,
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(NavRoutes.ParticipantBillDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val participantId = backStackEntry.arguments?.getString("participantId") ?: ""
            ParticipantBillDetailScreen(
                eventId = eventId,
                participantId = participantId,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}