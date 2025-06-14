// Perbaikan dan penyesuaian sesuai permintaan
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.viewModel.MainViewModel
import com.example.billbuddy.ui.components.AppIconButton
import com.example.billbuddy.ui.components.CommonNavigationBar
import com.example.billbuddy.ui.viewModel.AuthViewModel
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val backgroundColor = Color(0xFFFFDCDC)
    val buttonColor = Color(0xFFFFB6C1)
    val textColor = Color(0xFF4A4A4A)
    val premiumColor = Color(0xFFFFB6C1)

    val userProfile by mainViewModel.userProfile.observeAsState()
    var showMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
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
            Column {
                Button(
                    onClick = {
                        authViewModel.signOut()
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        navController.navigate(NavRoutes.Authentication.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Logout", color = Color.Red, fontWeight = FontWeight.Bold)
                }
                CommonNavigationBar(navController = navController, selectedScreen = "Profile")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                AppIconButton(
                    onClick = { /* Notifikasi */ },
                    icon = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier
                        .background(buttonColor, shape = RoundedCornerShape(50))
                        .size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                AppIconButton(
                    onClick = { showMenu = true },
                    icon = Icons.Default.MoreVert,
                    contentDescription = "More Options",
                    tint = Color.White,
                    modifier = Modifier
                        .background(buttonColor, shape = RoundedCornerShape(50))
                        .size(40.dp)
                )
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color.White)
                ) {}
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(buttonColor),
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
                    Text("👤", fontSize = 60.sp)
                }
                IconButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                        .padding(4.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Edit Photo", tint = buttonColor)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userProfile?.username ?: "Application User",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("PREMIUM", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = premiumColor)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.Check, contentDescription = "Premium", tint = premiumColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = userProfile?.email ?: "No email", fontSize = 16.sp, color = textColor)
            Spacer(modifier = Modifier.height(12.dp))
//            Text(text = userProfile?.bio ?: "No bio", fontSize = 14.sp, color = textColor)

//            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    username = userProfile?.username ?: ""
                    showEditDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Edit Profile", fontSize = 18.sp, color = textColor)
            }

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
        title = { Text("Edit Username") },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (newName.isNotBlank()) onSave(newName)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}
