package com.example.billbuddy.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.components.*
import com.example.billbuddy.ui.theme.*
import com.example.billbuddy.ui.viewModel.MainViewModel

@Composable
fun EventDetailScreen(
    eventId: String,
    viewModel: MainViewModel,
    navController: NavController
) {
    // Ambil data dari ViewModel
    val eventData by viewModel.eventData.observeAsState()
    val error by viewModel.error.observeAsState()
    var isLoading by remember { mutableStateOf(true) }

    // Ambil detail event dari database
    LaunchedEffect(eventId) {
        viewModel.getEventDetails(eventId)
        isLoading = false
    }

    // State untuk dialog konfirmasi hapus
    val showDeleteDialog = remember { mutableStateOf(false) }

    // Dialog untuk konfirmasi hapus
    if (showDeleteDialog.value) {
        Dialog(onDismissRequest = { showDeleteDialog.value = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Delete Event",
                        style = MaterialTheme.typography.displayMedium,
                        color = PinkButtonStroke
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Are you sure you want to delete this event?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkGreyText,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AppFilledButton(
                            onClick = { showDeleteDialog.value = false },
                            text = "Cancel",
                            containerColor = PinkButton,
                            textColor = White,
                            icon = null,
                            iconTint = null,
                            height = 40.dp,
                            fontSize = 16,
                            cornerRadius = 20.dp,
                            borderWidth = 2.dp,
                            borderColor = PinkButtonStroke
                        )
                        AppFilledButton(
                            onClick = {
                                viewModel.deleteEvent(eventId) {
                                    showDeleteDialog.value = false
                                    navController.popBackStack()
                                }
                            },
                            text = "Delete",
                            containerColor = PinkTua,
                            textColor = White,
                            icon = Icons.Default.Delete,
                            iconTint = White,
                            height = 40.dp,
                            fontSize = 16,
                            cornerRadius = 20.dp,
                            borderWidth = 2.dp,
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
                text = "Event Details",
                style = MaterialTheme.typography.displayLarge,
                color = PinkButtonStroke
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Konten utama
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                eventData?.let { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 15.dp, shape = RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Nama event dan pembuat
                            Text(
                                text = event.eventName,
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                                color = DarkGreyText,
                                fontFamily = KhulaExtrabold
                            )
                            Text(
                                text = "Creator: ${event.creatorName}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = DarkGreyText.copy(alpha = 0.6f),
                                fontFamily = RobotoFontFamily
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Detail transaksi
                            Text(
                                text = "Transaction Details",
                                style = MaterialTheme.typography.displayMedium,
                                color = PinkButtonStroke,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Daftar item
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(event.items) { item ->
                                    Log.d("EventDetailScreen", "Item: ${item.name}, UnitPrice: ${item.unitPrice}, Quantity: ${item.quantity}, Subtotal: ${item.unitPrice * item.quantity}")

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = DarkGreyText,
                                            fontFamily = KadwaFontFamily
                                        )
                                        Text(
                                            text = "${item.quantity}x",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = DarkGreyText,
                                            fontFamily = KadwaFontFamily
                                        )
                                        Text(
                                            text = "Rp ${item.unitPrice * item.quantity}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = DarkGreyText,
                                            fontFamily = KadwaFontFamily
                                        )
                                    }
                                    Divider(
                                        color = DarkGreyText.copy(alpha = 0.2f),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Hitung subtotal keseluruhan dari semua item
                            val subtotal = event.items.sumOf { item ->
                                (item.unitPrice * item.quantity).toLong()
                            }

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
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = DarkGreyText,
                                    fontFamily = KadwaFontFamily
                                )
                                Text(
                                    text = "Rp ${event.serviceFee}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = DarkGreyText,
                                    fontFamily = KadwaFontFamily
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
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = DarkGreyText,
                                    fontFamily = KadwaFontFamily
                                )
                                Text(
                                    text = "Rp ${event.taxAmount}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = DarkGreyText,
                                    fontFamily = KadwaFontFamily
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Hitung total akhir
                            val total = subtotal + event.taxAmount + event.serviceFee

                            Log.d("EventDetailScreen", "Total: $total, Tax: ${event.taxAmount}, ServiceFee: ${event.serviceFee}")

                            // Total
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(PinkBackground.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row {
                                    Text(
                                        text = "TOTAL",
                                        style = MaterialTheme.typography.displayMedium,
                                        color = PinkButtonStroke,
                                        fontFamily = KhulaExtrabold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "(${event.participants.size})",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = DarkGreyText,
                                        fontFamily = KadwaFontFamily
                                    )
                                }
                                Text(
                                    text = "Rp $total",
                                    style = MaterialTheme.typography.displayMedium,
                                    color = PinkButtonStroke,
                                    fontFamily = KhulaExtrabold
                                )
                            }
                        }
                    }
                } ?: error?.let {
                    Text(
                        text = "Error: $it",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol untuk navigasi ke ParticipantScreen
            AppFilledButton(
                onClick = {
                    if (eventId.isNotEmpty()) {
                        navController.navigate(NavRoutes.Participant.createRoute(eventId))
                    } else {
                        Log.e("EventDetailScreen", "Invalid eventId")
                    }
                },
                text = "View Participants",
                containerColor = PinkButton,
                textColor = White,
                icon = Icons.Default.Group,
                iconTint = White,
                modifier = Modifier.fillMaxWidth(),
                height = 60.dp,
                fontSize = 20,
                cornerRadius = 60.dp,
                borderWidth = 2.dp,
                borderColor = PinkButtonStroke
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Hapus Event
            AppFilledButton(
                onClick = { showDeleteDialog.value = true },
                text = "Delete Event",
                containerColor = PinkTua,
                textColor = White,
                icon = Icons.Default.Delete,
                iconTint = White,
                modifier = Modifier.fillMaxWidth(),
                height = 60.dp,
                fontSize = 20,
                cornerRadius = 60.dp,
                borderWidth = 2.dp,
                borderColor = PinkButtonStroke
            )
        }
    }
}