package com.example.billbuddy.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
                            // Log data item untuk debugging
                            Log.d("EventDetailScreen", "Item: ${item.name}, UnitPrice: ${item.unitPrice}, Quantity: ${item.quantity}, Subtotal: ${item.unitPrice * item.quantity}")

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

                    // Hitung subtotal keseluruhan dari semua item
                    val subtotal = event.items.sumOf { item ->
                        (item.unitPrice * item.quantity).toLong()
                    }

                    // Log subtotal untuk debugging
                    Log.d("EventDetailScreen", "Subtotal: $subtotal")

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

                    // Hitung total akhir
                    val total = subtotal + event.taxAmount + event.serviceFee

                    // Log total untuk debugging
                    Log.d("EventDetailScreen", "Total: $total, Tax: ${event.taxAmount}, ServiceFee: ${event.serviceFee}")

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
                            text = "Rp $total",
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

        // Tombol untuk navigasi ke ParticipantScreen
        Button(
            onClick = {
                navController.navigate("participant_screen/$eventId")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
        ) {
            Text(
                text = "View Participants",
                color = Color.White,
                fontSize = 18.sp
            )
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
    }
}