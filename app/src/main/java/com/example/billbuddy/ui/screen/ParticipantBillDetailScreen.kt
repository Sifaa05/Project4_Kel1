package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billbuddy.model.EventData
import com.example.billbuddy.model.Item
import com.example.billbuddy.model.Participant
import com.example.billbuddy.ui.MainViewModel
import com.example.billbuddy.util.Tuple4

@Composable
fun ParticipantBillDetailScreen(
    eventId: String,
    participantId: String,
    navController: NavController,
    viewModel: MainViewModel
) {
    // Ambil data dari ViewModel
    val eventData by viewModel.eventData.observeAsState()

    // Ambil detail event dari database
    LaunchedEffect(eventId) {
        viewModel.getEventDetails(eventId)
    }

    // Warna sesuai desain
    val backgroundColor = Color(0xFFFFDCDC) // Latar pink
    val buttonColor = Color(0xFFFFB6C1) // Warna tombol pink
    val textColor = Color(0xFF4A4A4A) // Warna teks abu-abu tua
    val accentColor = Color(0xFFFF6F61) // Warna aksen untuk divider dan ikon

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
                text = "Detail Bill",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                navController.popBackStack() // Kembali ke ParticipantScreen
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tampilkan detail tagihan
        eventData?.let { event ->
            // Temukan participant berdasarkan participantId
            val participant = event.participants.find { it.id == participantId }
            participant?.let { p ->
                // Hitung jumlah participant yang memilih setiap item
                val itemSelectionCount = mutableMapOf<String, Int>()
                event.items.forEach { item ->
                    val count = event.participants.count { participant ->
                        participant.itemsAssigned?.contains(item.itemId) == true
                    }
                    itemSelectionCount[item.itemId] = if (count > 0) count else 1
                }

                // Hitung jumlah total participant
                val participantCount = event.participants.size

                // Hitung subtotal, service fee, tax, dan total secara langsung
                val subtotal = event.items
                    .filter { p.itemsAssigned?.contains(it.itemId) == true }
                    .sumOf { item ->
                        val participantsForItem = itemSelectionCount[item.itemId] ?: 1
                        (item.totalPrice / participantsForItem).toLong()
                    }

                val serviceFee = event.serviceFee
                val taxAmount = event.taxAmount
                val participantServiceFee = if (participantCount > 0) serviceFee / participantCount else 0L
                val participantTax = if (participantCount > 0) taxAmount / participantCount else 0L

                val total = subtotal + participantServiceFee + participantTax

                // Daftar item yang dipilih oleh participant
                val selectedItems = event.items.filter { p.itemsAssigned?.contains(it.itemId) == true }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    // Nama participant
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Money,
                            contentDescription = "Bill Icon",
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = p.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Daftar item
                    if (selectedItems.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "Items Icon",
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Items",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(selectedItems) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textColor
                                    )
                                    Row {
                                        Text(
                                            text = "${item.quantity}x",
                                            fontSize = 16.sp,
                                            color = textColor
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Rp ${item.totalPrice / (itemSelectionCount[item.itemId] ?: 1)}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textColor
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No items selected",
                            fontSize = 14.sp,
                            color = textColor.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divider
                    Divider(
                        color = accentColor.copy(alpha = 0.3f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Rincian tagihan
                    Text(
                        text = "DETAIL BILL",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "SUBTOTAL",
                            fontSize = 14.sp,
                            color = textColor
                        )
                        Text(
                            text = "Rp $subtotal",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "+ TAX",
                            fontSize = 14.sp,
                            color = textColor
                        )
                        Text(
                            text = "Rp $participantTax",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "+ SERVICE FEE",
                            fontSize = 14.sp,
                            color = textColor
                        )
                        Text(
                            text = "Rp $participantServiceFee",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divider
                    Divider(
                        color = accentColor.copy(alpha = 0.3f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "TOTAL BILL",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor
                        )
                        Text(
                            text = "Rp $total",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor
                        )
                    }
                }
            } ?: Text(text = "Participant not found")
        } ?: Text(text = "Loading bill details...")
    }
}