package com.example.billbuddy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.billbuddy.repository.SplitBillRepository
import com.example.billbuddy.ui.viewModel.MainViewModel
import com.example.billbuddy.ui.viewModel.AuthViewModel
import com.example.billbuddy.ui.screen.AddBuddyScreen
import com.example.billbuddy.ui.screen.AssignItemsScreen
import com.example.billbuddy.ui.screen.authentication.AuthenticationScreen
import com.example.billbuddy.ui.screen.authentication.LoginScreen
import com.example.billbuddy.ui.screen.authentication.RegisterScreen
import com.example.billbuddy.ui.screen.EventDetailScreen
import com.example.billbuddy.ui.screen.HomeScreen
import com.example.billbuddy.ui.screen.InputEventScreen
import com.example.billbuddy.ui.screen.ListEventScreen
import com.example.billbuddy.ui.screen.ParticipantBillDetailScreen
import com.example.billbuddy.ui.screen.ParticipantScreen
import com.example.billbuddy.ui.screen.ProfileScreen
import com.example.billbuddy.ui.screen.SearchScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavHost(
    navController: NavHostController,
    auth: FirebaseAuth,
    authViewModel: AuthViewModel,
    repository: SplitBillRepository,
    mainViewModel: MainViewModel,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavRoutes.Authentication.route) {
            AuthenticationScreen(
                onLoginClick = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Authentication.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(NavRoutes.Register.route) {
                        popUpTo(NavRoutes.Authentication.route) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.Login.route) {
            LoginScreen(
                onLoginClick = {email, password ->
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                authViewModel.checkAuthState(auth.currentUser)
                                navController.navigate(NavRoutes.Home.route) {
                                    popUpTo(NavRoutes.Login.route) { inclusive = true }
                                }
                            }
                        }
                },
                onBackClick = {
                    navController.navigate(NavRoutes.Authentication.route) {
                        popUpTo(NavRoutes.Login.route) {inclusive = true}
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate(NavRoutes.ForgotPassword.route)
                }
            )
        }
        composable(NavRoutes.Register.route) {
            RegisterScreen(
                onCreateAccountClick = { email, password ->
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                authViewModel.checkAuthState(auth.currentUser)
                                navController.navigate(NavRoutes.Home.route) {
                                    popUpTo(NavRoutes.Register.route) { inclusive = true }
                                }
                            }
                        }
                },
                onBackClick = {
                    navController.navigate(NavRoutes.Authentication.route) {
                        popUpTo(NavRoutes.Register.route) {inclusive = true}
                    }
                }
            )
        }
        composable(NavRoutes.Home.route) {
            HomeScreen(navController = navController, viewModel = mainViewModel)
        }
        composable(NavRoutes.ListEvent.route) {
            ListEventScreen(navController = navController, viewModel = mainViewModel)
        }
        composable(NavRoutes.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(NavRoutes.Search.route) {
            SearchScreen(navController = navController, viewModel = mainViewModel)
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
                viewModel = mainViewModel,
                navController = navController
            )
        }
        composable(NavRoutes.AddBuddy.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            AddBuddyScreen(
                eventId = eventId,
                navController = navController,
                viewModel = mainViewModel
            )
        }
        composable(NavRoutes.AssignItems.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val selectedFriendsParam = backStackEntry.arguments?.getString("selectedFriendsParam") ?: ""
            AssignItemsScreen(
                eventId = eventId,
                selectedFriendsParam = selectedFriendsParam,
                navController = navController,
                viewModel = mainViewModel
            )
        }
        composable(NavRoutes.Participant.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            ParticipantScreen(
                eventId = eventId,
                viewModel = mainViewModel,
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
                viewModel = mainViewModel
            )
        }
    }
}