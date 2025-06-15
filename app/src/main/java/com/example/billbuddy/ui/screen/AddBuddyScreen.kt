package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.components.*
import com.example.billbuddy.ui.theme.*
import com.example.billbuddy.ui.viewModel.MainViewModel

@Composable
fun AddBuddyScreen(
    eventId: String,
    navController: NavController,
    viewModel: MainViewModel
) {
    // State untuk daftar teman
    val appUsersList = remember { mutableStateListOf("berlianavnti", "jhanmr", "sifasw") } // Pengguna aplikasi (statis)
    val nonAppUsersList = remember { mutableStateListOf<String>() } // Teman tanpa akun
    val filteredAppUsers = remember { mutableStateListOf<String>() }
    val searchQuery = remember { mutableStateOf("") }
    val selectedFriends = remember { mutableStateMapOf<String, Boolean>() }

    // State untuk dialog tambah teman baru
    val showAddFriendDialog = remember { mutableStateOf(false) }
    val newFriendName = remember { mutableStateOf("") }

    // Error dari ViewModel
    val error by viewModel.error.observeAsState()

    // Filter pengguna aplikasi berdasarkan pencarian
    LaunchedEffect(searchQuery.value) {
        filteredAppUsers.clear()
        filteredAppUsers.addAll(
            appUsersList.filter { it.contains(searchQuery.value, ignoreCase = true) }
        )
    }

    // Inisialisasi daftar pengguna aplikasi saat layar dimuat
    LaunchedEffect(Unit) {
        filteredAppUsers.clear()
        filteredAppUsers.addAll(appUsersList)
    }

    // Inisialisasi state untuk teman yang dipilih
    LaunchedEffect(appUsersList, nonAppUsersList) {
        appUsersList.forEach { friend ->
            if (!selectedFriends.containsKey(friend)) {
                selectedFriends[friend] = false
            }
        }
        nonAppUsersList.forEach { friend ->
            if (!selectedFriends.containsKey(friend)) {
                selectedFriends[friend] = false
            }
        }
    }

    // Hitung jumlah teman yang dipilih
    val selectedCount = selectedFriends.count { it.value }

    // Dialog untuk menambahkan teman baru
    if (showAddFriendDialog.value) {
        Dialog(onDismissRequest = { showAddFriendDialog.value = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Add New Friends",
                        style = MaterialTheme.typography.displayMedium,
                        color = PinkButtonStroke,
                        fontFamily = KhulaExtrabold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newFriendName.value,
                        onValueChange = { newFriendName.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TextFieldBackground, RoundedCornerShape(40.dp))
                            .shadow(elevation = 10.dp, shape = RoundedCornerShape(40.dp)),
                        label = { Text("Friends Name", style = MaterialTheme.typography.labelSmall, fontFamily = RobotoFontFamily) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = TextFieldBackground,
                            unfocusedContainerColor = TextFieldBackground,
                            focusedIndicatorColor = PinkButtonStroke,
                            unfocusedIndicatorColor = DarkGreyText,
                            focusedLabelColor = PinkButtonStroke,
                            unfocusedLabelColor = DarkGreyText
                        ),
                        shape = RoundedCornerShape(40.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AppFilledButton(
                            onClick = { showAddFriendDialog.value = false },
                            text = "Cancel",
                            containerColor = PinkTua,
                            textColor = White,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            height = 40.dp,
                            fontSize = 16,
                            cornerRadius = 20.dp,
                            borderWidth = 2.dp,
                            borderColor = PinkButtonStroke
                        )
                        AppFilledButton(
                            onClick = {
                                if (newFriendName.value.isNotBlank()) {
                                    nonAppUsersList.add(newFriendName.value)
                                    selectedFriends[newFriendName.value] = true
                                    showAddFriendDialog.value = false
                                    newFriendName.value = ""
                                }
                            },
                            text = "Add",
                            containerColor = PinkButton,
                            textColor = White,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            height = 40.dp,
                            fontSize = 16,
                            cornerRadius = 20.dp,
                            borderWidth = 2.dp,
                            //enabled = newFriendName.value.isNotBlank(),
                            borderColor = PinkButtonStroke
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            CommonNavigationBar(
                navController = navController,
                selectedScreen = "List"
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            HomeHeader(
                navController = navController,
                viewModel = viewModel,
                showBackButton = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Judul
            Text(
                text = "Choose Buddy",
                style = MaterialTheme.typography.displayLarge,
                color = PinkButtonStroke,
                fontFamily = KhulaExtrabold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Add Friend Without BillBuddy Account
            AppFilledButton(
                onClick = { showAddFriendDialog.value = true },
                text = "Add Friend Without BillBuddy Account",
                containerColor = PinkButton,
                textColor = White,
                icon = Icons.Default.Add,
                iconTint = White,
                modifier = Modifier.fillMaxWidth(),
                height = 60.dp,
                fontSize = 20,
                cornerRadius = 60.dp,
                borderWidth = 2.dp,
                borderColor = PinkButtonStroke
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Kolom Pencarian
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TextFieldBackground, RoundedCornerShape(40.dp))
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(40.dp)),
                placeholder = { Text("Enter Friends", style = MaterialTheme.typography.labelSmall, fontFamily = RobotoFontFamily) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = TextFieldBackground,
                    unfocusedContainerColor = TextFieldBackground,
                    focusedIndicatorColor = PinkButtonStroke,
                    unfocusedIndicatorColor = DarkGreyText,
                    focusedPlaceholderColor = DarkGreyText.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = DarkGreyText.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(40.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Daftar Teman
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Bagian Pengguna Aplikasi
                    item {
                        Text(
                            text = "Application Users",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = DarkGreyText,
                            fontFamily = KadwaFontFamily,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(filteredAppUsers) { friend ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = friend,
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkGreyText,
                                fontFamily = RobotoFontFamily,
                                modifier = Modifier.weight(1f)
                            )
                            Checkbox(
                                checked = selectedFriends[friend] ?: false,
                                onCheckedChange = { isChecked ->
                                    selectedFriends[friend] = isChecked
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PinkButton,
                                    uncheckedColor = DarkGreyText
                                )
                            )
                        }
                        Divider(
                            color = BlackText.copy(alpha = 0.2f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    // Bagian Teman Tanpa Akun
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Friends Without BillBuddy Accounts",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = DarkGreyText,
                            fontFamily = KadwaFontFamily,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    if (nonAppUsersList.isEmpty()) {
                        item {
                            Text(
                                text = "No friends without Buddy account yet.",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkGreyText.copy(alpha = 0.6f),
                                fontFamily = RobotoFontFamily
                            )
                        }
                    } else {
                        items(nonAppUsersList) { friend ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = friend,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkGreyText,
                                    fontFamily = RobotoFontFamily,
                                    modifier = Modifier.weight(1f)
                                )
                                Checkbox(
                                    checked = selectedFriends[friend] ?: false,
                                    onCheckedChange = { isChecked ->
                                        selectedFriends[friend] = isChecked
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = PinkButton,
                                        uncheckedColor = DarkGreyText
                                    )
                                )
                            }
                            Divider(
                                color = BlackText.copy(alpha = 0.2f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Add Buddy
            AppFilledButton(
                onClick = {
                    val selectedFriendsList = selectedFriends.filter { it.value }.keys.toList()
                    if (selectedFriendsList.isNotEmpty()) {
                        val selectedFriendsParam = selectedFriendsList.joinToString(",")
                        navController.navigate(NavRoutes.AssignItems.createRoute(eventId, selectedFriendsParam))
                    }
                },
                text = "Add Buddy ($selectedCount)",
                containerColor = PinkButton,
                textColor = White,
                icon = Icons.Default.Add,
                iconTint = White,
                modifier = Modifier.fillMaxWidth(),
                height = 60.dp,
                fontSize = 20,
                cornerRadius = 60.dp,
                borderWidth = 2.dp,
                //enabled = selectedCount > 0,
                borderColor = PinkButtonStroke
            )

            // Tampilkan error jika ada
            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Error: $it",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = RobotoFontFamily
                )
            }
        }
    }
}