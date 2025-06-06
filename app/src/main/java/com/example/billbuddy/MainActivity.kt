package com.example.billbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.billbuddy.repository.SplitBillRepository
import com.example.billbuddy.navigation.AppNavHost
import com.example.billbuddy.ui.viewModel.MainViewModel
import com.example.billbuddy.ui.viewModel.AuthViewModel
import com.example.billbuddy.ui.theme.BillBuddyTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var repository: SplitBillRepository
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        repository = SplitBillRepository()

        authViewModel.checkAuthState(auth.currentUser)

        setContent {
            BillBuddyTheme {
                Surface {
                    AppNavigation(
                        auth = auth,
                        authViewModel = authViewModel,
                        repository = repository,
                        mainViewModel = mainViewModel,
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
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val startDestination = authViewModel.startDestination.value

    AppNavHost(
        navController = navController,
        auth = auth,
        authViewModel = authViewModel,
        repository = repository,
        mainViewModel = mainViewModel,
        startDestination = startDestination,
        modifier = modifier
    )
}