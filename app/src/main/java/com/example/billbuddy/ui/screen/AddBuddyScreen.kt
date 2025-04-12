package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.billbuddy.ui.MainViewModel

@Composable
fun AddBuddyScreen(
    eventId: String,
    navController: NavController,
    viewModel: MainViewModel
) {
    // Warna sesuai desain
    val backgroundColor = Color(0xFFFFDCDC) // Latar pink
    val buttonColor = Color(0xFFFFB6C1) // Warna tombol pink
    val textColor = Color(0xFF4A4A4A) // Warna teks abu-abu tua

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
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Add New Friends",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newFriendName.value,
                        onValueChange = { newFriendName.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Friends Name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = buttonColor,
                            unfocusedIndicatorColor = textColor
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(
                            onClick = { showAddFriendDialog.value = false }
                        ) {
                            Text(
                                text = "Cancel",
                                color = buttonColor,
                                fontSize = 16.sp
                            )
                        }
                        TextButton(
                            onClick = {
                                if (newFriendName.value.isNotBlank()) {
                                    nonAppUsersList.add(newFriendName.value) // Tambahkan ke daftar teman tanpa akun
                                    selectedFriends[newFriendName.value] = true // Otomatis pilih teman baru
                                    showAddFriendDialog.value = false
                                    newFriendName.value = "" // Reset input
                                }
                            },
                            enabled = newFriendName.value.isNotBlank()
                        ) {
                            Text(
                                text = "Add",
                                color = if (newFriendName.value.isNotBlank()) buttonColor else Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header dengan tombol close
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Choose Buddy",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                navController.popBackStack() // Kembali ke EventDetailScreen
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Add Friend Without BillBuddy Account
        Button(
            onClick = {
                showAddFriendDialog.value = true // Tampilkan dialog untuk menambah teman baru
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
        ) {
            Text(
                text = "Add Friend Without BillBuddy Account",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Kolom Pencarian
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp)),
            placeholder = { Text("Enter Friends") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = buttonColor,
                unfocusedIndicatorColor = textColor
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Daftar Teman
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Bagian Pengguna Aplikasi
            item {
                Text(
                    text = "Application Users",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
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
                        fontSize = 16.sp,
                        color = textColor,
                        modifier = Modifier.weight(1f)
                    )
                    Checkbox(
                        checked = selectedFriends[friend] ?: false,
                        onCheckedChange = { isChecked ->
                            selectedFriends[friend] = isChecked
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = buttonColor,
                            uncheckedColor = textColor
                        )
                    )
                }
            }

            // Bagian Teman Tanpa Akun
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Friends Without BillBuddy Accounts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            if (nonAppUsersList.isEmpty()) {
                item {
                    Text(
                        text = "No friends without Buddy account yet.",
                        fontSize = 14.sp,
                        color = textColor.copy(alpha = 0.6f)
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
                            fontSize = 16.sp,
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = selectedFriends[friend] ?: false,
                            onCheckedChange = { isChecked ->
                                selectedFriends[friend] = isChecked
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = buttonColor,
                                uncheckedColor = textColor
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Add Buddy
        Button(
            onClick = {
                val selectedFriendsList = selectedFriends.filter { it.value }.keys.toList()
                if (selectedFriendsList.isNotEmpty()) {
                    // Encode daftar teman yang dipilih sebagai parameter navigasi
                    val selectedFriendsParam = selectedFriendsList.joinToString(",")
                    navController.navigate("assign_items_screen/$eventId/$selectedFriendsParam")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            enabled = selectedCount > 0
        ) {
            Text(
                text = "Add Buddy ($selectedCount)",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        // Tampilkan error jika ada
        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Error: $it",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}