package com.example.billbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.billbuddy.data.SplitBillRepository
import com.example.billbuddy.navigation.AppNavHost
import com.example.billbuddy.ui.MainViewModel
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
    AppNavHost(
        navController = navController,
        repository = repository,
        viewModel = viewModel,
        modifier = modifier
    )
}