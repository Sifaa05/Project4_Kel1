package com.example.billbuddy.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.billbuddy.ui.MainViewModel

@Composable
fun EventDetailScreen(eventId: String, viewModel: MainViewModel = viewModel()) {
    val eventData by viewModel.eventData.observeAsState()
    val error by viewModel.error.observeAsState()

    LaunchedEffect(eventId) {
        viewModel.getEventDetails(eventId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        error?.let {
            Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        eventData?.let { event ->
            Text(text = event.eventName, style = MaterialTheme.typography.headlineMedium)
            Text(text = "Creator: ${event.creatorName}")
            Text(text = "Total: ${event.totalAmount} (Tax: ${event.taxAmount}, Service: ${event.serviceFee})")
            Text(text = "Status: ${event.status}")
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Items:", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(event.items) { item ->
                    Text(text = "${item.name} (x${item.quantity}): ${item.price}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Participants:", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(event.participants) { participant ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = { viewModel.updatePaymentStatus(event.eventId, participant.id, !participant.paid) },
                            enabled = participant.isCreator // Hanya creator bisa ubah status
                        ) {
                            Text(if (participant.paid) "Mark Unpaid" else "Mark Paid")
                        }
                    }
                }
            }
        }
    }
}