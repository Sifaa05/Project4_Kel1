package com.example.billbuddy.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.theme.PinkButtonStroke

@Composable
fun CommonNavigationBar(
    navController: NavController,
    selectedScreen: String
) {
    NavigationBar(
        containerColor = PinkButtonStroke // Pink tua untuk navbar
    ) {
        NavigationBarItem(
            selected = selectedScreen == "Home",
            onClick = { navController.navigate(NavRoutes.Home.route) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = selectedScreen == "List",
            onClick = { navController.navigate(NavRoutes.ListEvent.route) },
            icon = { Icon(Icons.Filled.List, contentDescription = "List") },
            label = { Text("List") }
        )
        NavigationBarItem(
            selected = selectedScreen == "Profile",
            onClick = { navController.navigate(NavRoutes.Profile.route) },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
    }
}