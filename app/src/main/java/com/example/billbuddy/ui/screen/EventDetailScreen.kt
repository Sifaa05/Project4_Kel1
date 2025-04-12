package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.billbuddy.ui.MainViewModel

@Composable
fun EventDetailScreen(
    eventId: String,
    viewModel: MainViewModel,
    navController: NavController
) {
    // Ambil data dari ViewModel
    val eventData by viewModel.eventData.observeAsState()
    val error by viewModel.error.observeAsState()

    // Ambil detail event dari database
    LaunchedEffect(eventId) {
        viewModel.getEventDetails(eventId)
    }

    // State untuk dialog konfirmasi hapus
    val showDeleteDialog = remember { mutableStateOf(false) }

    // Warna sesuai desain
    val backgroundColor = Color(0xFFFFDCDC) // Latar pink
    val buttonColor = Color(0xFFFFB6C1) // Warna tombol pink
    val textColor = Color(0xFF4A4A4A) // Warna teks abu-abu tua
    val paidButtonColor = Color(0xFF6A5ACD) // Warna tombol Mark Unpaid (ungu)
    val deleteButtonColor = Color(0xFFFF4444) // Warna tombol hapus (merah)

    // Dialog untuk konfirmasi hapus
    if (showDeleteDialog.value) {
        Dialog(onDismissRequest = { showDeleteDialog.value = false }) {
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
                        text = "Delete Event",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Are you sure you want to delete this event?",
                        fontSize = 16.sp,
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(
                            onClick = { showDeleteDialog.value = false }
                        ) {
                            Text(
                                text = "Cancel",
                                color = buttonColor,
                                fontSize = 16.sp
                            )
                        }
                        TextButton(
                            onClick = {
                                viewModel.deleteEvent(eventId) {
                                    showDeleteDialog.value = false
                                    navController.popBackStack() // Kembali ke halaman sebelumnya
                                }
                            }
                        ) {
                            Text(
                                text = "Delete",
                                color = deleteButtonColor,
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
                text = "Split Bill",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                navController.popBackStack() // Kembali ke halaman sebelumnya
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Kotak konten dengan border putih
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.White, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                eventData?.let { event ->
                    // Ikon grup (placeholder, bisa diganti dengan gambar asli)
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.Gray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👥", fontSize = 40.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Nama event dan pembuat
                    Text(
                        text = event.eventName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = "Creator: ${event.creatorName}",
                        fontSize = 16.sp,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Detail transaksi
                    Text(
                        text = "Transaction Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Daftar item
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(event.items) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.name,
                                    fontSize = 16.sp,
                                    color = textColor
                                )
                                Text(
                                    text = "${item.quantity}x",
                                    fontSize = 16.sp,
                                    color = textColor
                                )
                                Text(
                                    text = "Rp ${item.unitPrice * item.quantity}",
                                    fontSize = 16.sp,
                                    color = textColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Service Fee
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Service Fee",
                            fontSize = 16.sp,
                            color = textColor
                        )
                        Text(
                            text = "Rp ${event.serviceFee}",
                            fontSize = 16.sp,
                            color = textColor
                        )
                    }

                    // Tax
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tax",
                            fontSize = 16.sp,
                            color = textColor
                        )
                        Text(
                            text = "Rp ${event.taxAmount}",
                            fontSize = 16.sp,
                            color = textColor
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Text(
                                text = "TOTAL",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(${event.participants.size})",
                                fontSize = 18.sp,
                                color = textColor
                            )
                        }
                        Text(
                            text = "Rp ${event.totalAmount}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                } ?: error?.let {
                    Text(
                        text = "Error: $it",
                        color = MaterialTheme.colorScheme.error
                    )
                } ?: Text(text = "Loading event details...")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Add Buddy
        Button(
            onClick = {
                navController.navigate("add_buddy_screen/$eventId") // Navigasi ke AddBuddyScreen
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Buddy",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Buddy",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tombol Hapus Event
        Button(
            onClick = {
                showDeleteDialog.value = true // Tampilkan dialog konfirmasi hapus
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = deleteButtonColor)
        ) {
            Text(
                text = "Delete Event",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Daftar peserta
        eventData?.let { event ->
            Text(
                text = "Participants:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(event.participants) { participant ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            if (participant.isCreator) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Creator",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = "${participant.name}: ${participant.amount} (Paid: ${participant.paid})",
                                fontSize = 16.sp,
                                color = textColor
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.updatePaymentStatus(
                                    event.eventId,
                                    participant.id,
                                    !participant.paid
                                )
                            },
                            enabled = participant.isCreator, // Hanya creator yang bisa ubah status
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (participant.paid) paidButtonColor else Color.Gray
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (participant.paid) "Mark Unpaid" else "Mark Paid",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}