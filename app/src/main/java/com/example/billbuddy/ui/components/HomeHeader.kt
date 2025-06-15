package com.example.billbuddy.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.theme.PinkButtonStroke
import com.example.billbuddy.ui.viewModel.MainViewModel

@Composable
fun HomeHeader(
    navController: NavController,
    viewModel: MainViewModel,
    showBackButton: Boolean = false,
    showLogoutButton: Boolean = false,
    showSearchButton: Boolean = true,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.observeAsState()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBackButton) {
            AppIconButton(
                onClick = { navController.popBackStack() },
                icon = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = PinkButtonStroke,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = "Hello, ${userProfile?.name ?: "Buddy"}!",
                style = MaterialTheme.typography.displayMedium,
                color = PinkButtonStroke
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        AppIconButton(
            onClick = { navController.navigate(NavRoutes.Notification.route) },
            icon = Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = PinkButtonStroke,
            modifier = Modifier.size(24.dp)
        )
        if (showSearchButton) {
            AppIconButton(
                onClick = { navController.navigate(NavRoutes.Search.route) },
                icon = Icons.Default.Search,
                contentDescription = "Search",
                tint = PinkButtonStroke,
                modifier = Modifier.size(24.dp)
            )
        }
        if (showLogoutButton) {
            AppIconButton(
                onClick = onLogout,
                icon = Icons.Default.ExitToApp,
                contentDescription = "Logout",
                tint = PinkButtonStroke,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}