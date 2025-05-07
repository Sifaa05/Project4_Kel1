package com.example.billbuddy.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.theme.Pink40

@Composable
fun HomeHeader(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Hello, Buddy!",
            style = MaterialTheme.typography.titleLarge,
            color = Pink40 // Pink tua untuk header
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { /* TODO: Navigasi ke NotificationScreen */ }) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Pink40,
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(onClick = { navController.navigate(NavRoutes.Search.route) }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Pink40,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}