package com.example.billbuddy

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.billbuddy.repository.SplitBillRepository
import com.example.billbuddy.navigation.AppNavHost
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.repository.UserRepository
import com.example.billbuddy.ui.viewModel.MainViewModel
import com.example.billbuddy.ui.viewModel.AuthViewModel
import com.example.billbuddy.ui.theme.BillBuddyTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val authViewModel: AuthViewModel by viewModels()
    private val splitBillRepository: SplitBillRepository by lazy { SplitBillRepository() }
    private val userRepository: UserRepository by lazy { UserRepository() }
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        // Inisialisasi SharedPreferences
        val sharedPreferences = getSharedPreferences("BillBuddyPrefs", Context.MODE_PRIVATE)

        // Selalu set startDestination ke splash_screen
        authViewModel.setStartDestinationToSplash()

        setContent {
            BillBuddyTheme {
                Surface {
                    AppNavigation(
                        auth = auth,
                        authViewModel = authViewModel,
                        repository = splitBillRepository,
                        mainViewModel = mainViewModel,
                        userRepository = userRepository,
                        sharedPreferences = sharedPreferences,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    auth: FirebaseAuth,
    authViewModel: AuthViewModel,
    repository: SplitBillRepository,
    mainViewModel: MainViewModel,
    userRepository: UserRepository,
    sharedPreferences: android.content.SharedPreferences,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    // Reset navigasi ke splash_screen setiap kali aplikasi dibuka
    LaunchedEffect(Unit) {
        navController.navigate(NavRoutes.Splash.route) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    AppNavHost(
        navController = navController,
        auth = auth,
        authViewModel = authViewModel,
        repository = repository,
        mainViewModel = mainViewModel,
        userRepository = userRepository,
        sharedPreferences = sharedPreferences,
        startDestination = authViewModel.startDestination.value,
        modifier = modifier
    )
}