package com.example.billbuddy.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.components.*
import com.example.billbuddy.ui.theme.*
import com.example.billbuddy.ui.viewModel.AuthViewModel
import com.example.billbuddy.ui.viewModel.MainViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val userProfile by mainViewModel.userProfile.observeAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            mainViewModel.uploadProfilePhoto(it) {
                Toast.makeText(context, "Profile photo updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        mainViewModel.getUserProfile()
    }

    Scaffold(
        bottomBar = {
            CommonNavigationBar(navController = navController, selectedScreen = "Profile")
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeHeader(
                navController = navController,
                viewModel = mainViewModel,
                showBackButton = false,
                showLogoutButton = true,
                showSearchButton = false,
                onLogout = { showLogoutDialog = true }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(CardBackground),
                    contentAlignment = Alignment.Center
                ) {
                    if (!userProfile?.photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = userProfile?.photoUrl,
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("👤", fontSize = 60.sp, color = DarkGreyText)
                    }
                }
                AppIconButton(
                    onClick = { launcher.launch("image/*") },
                    icon = Icons.Default.CameraAlt,
                    contentDescription = "Edit Photo",
                    tint = PinkButton,
                    modifier = Modifier
                        .offset(x = 90.dp, y = 90.dp)
                        .size(32.dp)
                        .background(White, CircleShape)
                        .padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = userProfile?.name ?: "Anonymous",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = DarkGreyText
            )
            if (!userProfile?.username.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "@${userProfile?.username}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkGreyText
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = userProfile?.email ?: "No email",
                style = MaterialTheme.typography.bodyLarge,
                color = DarkGreyText
            )
            Spacer(modifier = Modifier.height(24.dp))
            AppFilledButton(
                onClick = {
                    username = userProfile?.username ?: ""
                    showEditDialog = true
                },
                text = "Edit Profile",
                textColor = White,
                containerColor = PinkButton,
                borderColor = PinkButtonStroke,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp),
                cornerRadius = 25.dp,
                fontSize = 18,
                fontFamily = KadwaFontFamily
            )
            if (showEditDialog) {
                EditUsernameDialog(
                    initialUsername = username,
                    onSave = {
                        mainViewModel.updateUsername(it)
                        showEditDialog = false
                    },
                    onCancel = { showEditDialog = false }
                )
            }
            if (showLogoutDialog) {
                LogoutConfirmationDialog(
                    onConfirm = {
                        authViewModel.signOut()
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        navController.navigate(NavRoutes.Authentication.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                        showLogoutDialog = false
                    },
                    onCancel = { showLogoutDialog = false }
                )
            }
        }
    }
}

@Composable
fun EditUsernameDialog(
    initialUsername: String,
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    var newName by remember { mutableStateOf(initialUsername) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = "Edit Username",
                style = MaterialTheme.typography.displayMedium,
                color = DarkGreyText
            )
        },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Username", color = DarkGreyText) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedIndicatorColor = PinkButtonStroke,
                    unfocusedIndicatorColor = PinkButtonStroke,
                    cursorColor = PinkButton,
                    focusedLabelColor = DarkGreyText,
                    unfocusedLabelColor = DarkGreyText
                ),
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = KadwaFontFamily,
                    color = DarkGreyText
                )
            )
        },
        confirmButton = {
            AppTextButton(
                onClick = {
                    if (newName.isNotBlank()) onSave(newName)
                },
                text = "Save",
                textColor = PinkButtonStroke
            )
        },
        dismissButton = {
            AppTextButton(
                onClick = onCancel,
                text = "Cancel",
                textColor = DarkGreyText
            )
        },
        containerColor = White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = "Confirm Logout",
                style = MaterialTheme.typography.displayMedium,
                color = DarkGreyText
            )
        },
        text = {
            Text(
                text = "Are you sure you want to log out?",
                style = MaterialTheme.typography.labelSmall,
                color = DarkGreyText
            )
        },
        confirmButton = {
            AppFilledButton(
                onClick = onConfirm,
                text = "Confirm",
                textColor = White,
                containerColor = PinkButton,
                borderColor = PinkButtonStroke,
                modifier = Modifier.height(40.dp),
                cornerRadius = 20.dp,
                fontSize = 14,
                fontFamily = KadwaFontFamily
            )
        },
        dismissButton = {
            AppTextButton(
                onClick = onCancel,
                text = "Cancel",
                textColor = DarkGreyText
            )
        },
        containerColor = White,
        shape = RoundedCornerShape(16.dp)
    )
}