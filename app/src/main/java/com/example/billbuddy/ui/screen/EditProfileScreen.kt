package com.example.billbuddy.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billbuddy.ui.components.AppLoadingButton
import com.example.billbuddy.ui.theme.*
import com.example.billbuddy.ui.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val userProfile by mainViewModel.userProfile.observeAsState()
    val error by mainViewModel.error.observeAsState()
    var name by remember { mutableStateOf(userProfile?.name ?: "") }
    var username by remember { mutableStateOf(userProfile?.username ?: "") }
    var isLoading by remember { mutableStateOf(false) }

    // Regex for username: letters, numbers, underscore, no spaces, 3-20 characters
    val usernameRegex = Regex("^[a-zA-Z0-9_]{3,20}$")

    // Update fields when userProfile changes
    LaunchedEffect(userProfile) {
        userProfile?.let {
            name = it.name
            username = it.username ?: ""
        }
    }

    // Show error messages
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            //mainViewModel.error.value = null // Clear error after showing
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        fontFamily = KadwaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = DarkGreyText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkGreyText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = DarkGreyText
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = "Name Icon") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(60.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(40.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = TextFieldBackground,
                    unfocusedContainerColor = TextFieldBackground,
                    focusedIndicatorColor = PinkButtonStroke,
                    unfocusedIndicatorColor = PinkButtonStroke,
                    cursorColor = DarkGreyText,
                    focusedLabelColor = DarkGreyText.copy(alpha = 0.58f),
                    unfocusedLabelColor = DarkGreyText.copy(alpha = 0.58f)
                ),
                shape = RoundedCornerShape(40.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = KadwaFontFamily,
                    color = DarkGreyText.copy(alpha = 0.58f),
                    fontSize = 12.sp
                ),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = "Username Icon") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(60.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(40.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = TextFieldBackground,
                    unfocusedContainerColor = TextFieldBackground,
                    focusedIndicatorColor = PinkButtonStroke,
                    unfocusedIndicatorColor = PinkButtonStroke,
                    cursorColor = DarkGreyText,
                    focusedLabelColor = DarkGreyText.copy(alpha = 0.58f),
                    unfocusedLabelColor = DarkGreyText.copy(alpha = 0.58f)
                ),
                shape = RoundedCornerShape(40.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = KadwaFontFamily,
                    color = DarkGreyText.copy(alpha = 0.58f),
                    fontSize = 12.sp
                ),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppLoadingButton(
                onClick = {
                    if (name.isEmpty()) {
                        Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                    } else if (!username.matches(usernameRegex)) {
                        Toast.makeText(
                            context,
                            "Username must be 3-20 characters, letters, numbers, or underscores",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        isLoading = true
                        mainViewModel.updateName(name) {
                            mainViewModel.updateUsername(username) {
                                isLoading = false
                                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    }
                },
                text = "Save Changes",
                loadingText = "Saving...",
                isLoading = isLoading,
                textColor = White,
                containerColor = PinkButton,
                borderColor = PinkButtonStroke,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp),
                cornerRadius = 25.dp,
                fontSize = 18,
                fontFamily = KadwaFontFamily,
                enabled = !isLoading
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 16.dp),
                    color = PinkButton
                )
            }
        }
    }
}