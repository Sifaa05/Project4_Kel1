package com.example.billbuddy.ui.screen

import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.billbuddy.ui.components.AppIconButton
import com.example.billbuddy.ui.components.CommonNavigationBar
import com.example.billbuddy.ui.viewModel.AuthViewModel
import com.example.billbuddy.ui.viewModel.MainViewModel
import android.widget.Toast
import com.example.billbuddy.navigation.NavRoutes
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    // Warna sesuai desain
    val backgroundColor = Color(0xFFFFDCDC) // Latar pink
    val buttonColor = Color(0xFFFFB6C1) // Warna tombol pink
    val textColor = Color(0xFF4A4A4A) // Warna teks abu-abu tua
    val premiumColor = Color(0xFFFFB6C1) // Warna pink untuk status premium

    // State untuk mengontrol visibilitas DropdownMenu
    var showMenu by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val userProfile by mainViewModel.userProfile.observeAsState(initial = null)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            mainViewModel.uploadProfilePhoto(it)
            Toast.makeText(context, "Uploading profile photo...", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        mainViewModel.getUserProfile()
    }

    Scaffold(
        bottomBar = {
            CommonNavigationBar(
                navController = navController,
                selectedScreen = "Profile"
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header dengan tombol notifikasi dan menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                // Tombol Notifikasi
                AppIconButton(
                    onClick = { /* TODO: Aksi notifikasi */ },
                    icon = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier
                        .background(buttonColor, shape = RoundedCornerShape(50))
                        .size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Tombol Menu (tiga titik)
                AppIconButton(
                    onClick = { showMenu = !showMenu },
                    icon = Icons.Default.MoreVert,
                    contentDescription = "More Options",
                    tint = Color.White,
                    modifier = Modifier
                        .background(buttonColor, shape = RoundedCornerShape(50))
                        .size(40.dp)
                )
                // Dropdown Menu
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    // Opsi Logout
                    DropdownMenuItem(
                        text = { Text("Logout", color = textColor, fontSize = 16.sp) },
                        onClick = {
                            authViewModel.signOut()
                            Toast.makeText(context, "Berhasil logout", Toast.LENGTH_SHORT).show()
                            navController.navigate(NavRoutes.Authentication.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                            showMenu = false
                        }
                    )
                    // Opsi Sign in ke akun lain
                    DropdownMenuItem(
                        text = { Text("Sign in ke akun lain", color = textColor, fontSize = 16.sp) },
                        onClick = {
                            authViewModel.signOut()
                            Toast.makeText(context, "Silakan sign in dengan akun lain", Toast.LENGTH_SHORT).show()
                            navController.navigate(NavRoutes.Authentication.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                            showMenu = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Foto Profil (Placeholder)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(buttonColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (userProfile?.photoUrl != null) {
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
                        .size(30.dp)
                        .background(Color.White, CircleShape)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = buttonColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nama Pengguna
            Text(
                text = userProfile?.username ?: userProfile?.name ?: "Application Users",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status Keanggotaan
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PREMIUM",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = premiumColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Premium Status",
                    tint = premiumColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    username = userProfile?.username ?: ""
                    showDialog = true
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

            if (showDialog) {
                showDialog(
                    navController = navController,
                    mainViewModel = mainViewModel,
                    initialUsername = username,
                    onDismiss = { showDialog = false }
                )
            }
        }
    }
}

@Composable
fun showDialog(
    navController: NavController,
    mainViewModel: MainViewModel,
    initialUsername: String,
    onDismiss: () -> Unit
) {
    var username by remember { mutableStateOf(initialUsername) }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit Username") },
        text = {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (username.isNotEmpty()) {
                        mainViewModel.updateUsername(username)
                        onDismiss()
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
} 