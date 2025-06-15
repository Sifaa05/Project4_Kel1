package com.example.billbuddy.navigation

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.billbuddy.repository.SplitBillRepository
import com.example.billbuddy.repository.UserRepository
import com.example.billbuddy.ui.screen.AddBuddyScreen
import com.example.billbuddy.ui.screen.AssignItemsScreen
import com.example.billbuddy.ui.screen.authentication.AuthenticationScreen
import com.example.billbuddy.ui.screen.authentication.LoginScreen
import com.example.billbuddy.ui.screen.authentication.RegisterScreen
import com.example.billbuddy.ui.screen.authentication.VerificationScreen
import com.example.billbuddy.ui.screen.authentication.ForgotPasswordScreen
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
import com.example.billbuddy.ui.screen.ScanScreen
import com.example.billbuddy.ui.screen.SearchScreen
import com.example.billbuddy.ui.screen.SplashScreen
import com.example.billbuddy.ui.viewModel.AuthViewModel
import com.example.billbuddy.ui.viewModel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavHost(
    navController: NavHostController,
    auth: FirebaseAuth,
    authViewModel: AuthViewModel,
    repository: SplitBillRepository,
    userRepository: UserRepository,
    mainViewModel: MainViewModel,
    sharedPreferences: SharedPreferences,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Splash Screen
        composable(NavRoutes.Splash.route) {
            SplashScreen(navController = navController, sharedPreferences = sharedPreferences, auth = auth)
        }

        // Onboarding Routes
        composable(NavRoutes.OnboardingSatu.route) {
            OnboardingSatuScreen(navController = navController)
        }
        composable(NavRoutes.OnboardingDua.route) {
            OnboardingDuaScreen(navController = navController)
        }
        composable(NavRoutes.OnboardingTiga.route) {
            OnboardingTigaScreen(navController = navController, sharedPreferences = sharedPreferences)
        }

        // Authentication Routes
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
                onLoginClick = { email, password ->
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                authViewModel.checkAuthState(auth.currentUser, true)
                                navController.navigate(NavRoutes.Home.route) {
                                    popUpTo(NavRoutes.Login.route) { inclusive = true }
                                }
                            }
                        }
                },
                onBackClick = {
                    navController.navigate(NavRoutes.Authentication.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate(NavRoutes.ForgotPassword.route)
                }
            )
        }
        composable(NavRoutes.ForgotPassword.route) {
            ForgotPasswordScreen(
                onResetPasswordClick = { email -> },
                onBackClick = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.ForgotPassword.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.ForgotPassword.route) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.Register.route) {
            RegisterScreen(
                onCreateAccountClick = { email, password ->
                    navController.navigate(NavRoutes.Verification.route) {
                                    popUpTo(NavRoutes.Register.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.navigate(NavRoutes.Authentication.route) {
                        popUpTo(NavRoutes.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.Verification.route) {
            VerificationScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                email = auth.currentUser?.email ?: "email@email.com"
            )
        }

        // Main App Routes
        composable(NavRoutes.Home.route) {
            HomeScreen(navController = navController, viewModel = mainViewModel)
        }
        composable(NavRoutes.ListEvent.route) {
            ListEventScreen(navController = navController, viewModel = mainViewModel)
        }
        composable(NavRoutes.Profile.route) {
            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel,
                mainViewModel = mainViewModel
            )
        }
        composable(NavRoutes.Search.route) {
            SearchScreen(navController = navController, viewModel = mainViewModel)
        }

        // Event-Related Routes
        composable(
            route = NavRoutes.InputEvent.route,
            arguments = listOf(
                navArgument("scannedBillDataJson") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            InputEventScreen(
                navController = navController,
                repository = repository,
                scannedBillDataJson = backStackEntry.arguments?.getString("scannedBillDataJson")
            )
        }
        composable(
            route = NavRoutes.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(
                eventId = eventId,
                viewModel = mainViewModel,
                navController = navController
            )
        }
        composable(
            route = NavRoutes.AddBuddy.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            AddBuddyScreen(
                eventId = eventId,
                navController = navController,
                viewModel = mainViewModel
            )
        }
        composable(
            route = NavRoutes.AssignItems.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType },
                navArgument("selectedFriendsParam") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val selectedFriendsParam = backStackEntry.arguments?.getString("selectedFriendsParam") ?: ""
            AssignItemsScreen(
                eventId = eventId,
                selectedFriendsParam = selectedFriendsParam,
                navController = navController,
                viewModel = mainViewModel
            )
        }
        composable(
            route = NavRoutes.Participant.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            ParticipantScreen(
                eventId = eventId,
                viewModel = mainViewModel,
                navController = navController
            )
        }
        composable(
            route = NavRoutes.ParticipantBillDetail.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType },
                navArgument("participantId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val participantId = backStackEntry.arguments?.getString("participantId") ?: ""
            ParticipantBillDetailScreen(
                eventId = eventId,
                participantId = participantId,
                navController = navController,
                viewModel = mainViewModel
            )
        }

        // Scan Route
        composable(NavRoutes.Scan.route) {
            ScanScreen(
                navController = navController,
                viewModel = mainViewModel
            )
        }
    }
}