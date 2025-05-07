package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.navigation.NavController
import com.example.billbuddy.model.EventData
import com.example.billbuddy.model.Participant
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.MainViewModel
import com.example.billbuddy.ui.components.AppFilledButton
import com.example.billbuddy.ui.components.AppIconButton
import com.example.billbuddy.ui.components.CommonNavigationBar
import com.example.billbuddy.util.Tuple4

@Composable
fun ParticipantScreen(
    eventId: String,
    viewModel: MainViewModel,
    navController: NavController
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
    val paidButtonColor = Color(0xFF6A5ACD) // Warna tombol Mark Unpaid (ungu)

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
                .background(backgroundColor)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header dengan tombol kembali
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconButton(
                    onClick = { navController.popBackStack() },
                    icon = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .background(buttonColor, shape = RoundedCornerShape(50))
                        .size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Participants",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Add Buddy
            AppFilledButton(
                onClick = { navController.navigate(NavRoutes.AddBuddy.createRoute(eventId)) },
                text = "Add Buddy",
                containerColor = buttonColor,
                textColor = Color.White,
                icon = Icons.Default.Add,
                iconTint = Color.White,
                modifier = Modifier.fillMaxWidth(),
                //fontSize = 18f,
                height = 56.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Daftar peserta
            eventData?.let { event ->
                Text(
                    text = "Participants (${event.participants.size}):",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

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

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(event.participants) { participant ->
                        // Hitung subtotal, service fee, tax, dan total untuk participant ini
                        val (subtotal, participantServiceFee, participantTax, total) = calculateParticipantDetails(
                            participant = participant,
                            event = event,
                            itemSelectionCount = itemSelectionCount,
                            participantCount = participantCount
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                                        text = "${participant.name} (Paid: ${participant.paid})",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                }
                                Row {
                                    // Tombol Mark Paid/Unpaid
                                    AppFilledButton(
                                        onClick = {
                                            viewModel.updatePaymentStatus(
                                                event.eventId,
                                                participant.id,
                                                !participant.paid
                                            )
                                        },
                                        text = if (participant.paid) "Mark Unpaid" else "Mark Paid",
                                        containerColor = if (participant.paid) paidButtonColor else Color.Gray,
                                        textColor = Color.White,
                                        modifier = Modifier
                                            .height(36.dp)
                                            .padding(end = 8.dp),
                                        //fontSize = 12f,
                                        height = 36.dp
                                    )
                                    // Tombol Detail Bill
                                    AppFilledButton(
                                        onClick = {
                                            navController.navigate(NavRoutes.ParticipantBillDetail.createRoute(eventId, participant.id))
                                        },
                                        text = "Detail Bill",
                                        containerColor = buttonColor,
                                        textColor = Color.White,
                                        modifier = Modifier.height(36.dp),
                                        //fontSize = 12f,
                                        height = 36.dp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Tampilkan rincian: Subtotal, Service Fee, Tax, Total
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Subtotal",
                                    fontSize = 14.sp,
                                    color = textColor
                                )
                                Text(
                                    text = "Rp $subtotal",
                                    fontSize = 14.sp,
                                    color = textColor
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Service Fee",
                                    fontSize = 14.sp,
                                    color = textColor
                                )
                                Text(
                                    text = "Rp $participantServiceFee",
                                    fontSize = 14.sp,
                                    color = textColor
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Tax",
                                    fontSize = 14.sp,
                                    color = textColor
                                )
                                Text(
                                    text = "Rp $participantTax",
                                    fontSize = 14.sp,
                                    color = textColor
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Text(
                                    text = "Rp $total",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            } ?: Text(text = "Loading participants...")
        }
    }
}

@Composable
fun calculateParticipantDetails(
    participant: Participant,
    event: EventData,
    itemSelectionCount: Map<String, Int>,
    participantCount: Int
): Tuple4<Long, Long, Long, Long> {
    // Hitung subtotal berdasarkan item yang dipilih oleh participant
    val subtotal = event.items
        .filter { participant.itemsAssigned?.contains(it.itemId) == true }
        .sumOf { item ->
            val participantsForItem = itemSelectionCount[item.itemId] ?: 1
            (item.totalPrice / participantsForItem).toLong()
        }

    // Hitung service fee dan tax berdasarkan jumlah participant
    val serviceFee = event.serviceFee
    val taxAmount = event.taxAmount
    val participantServiceFee = if (participantCount > 0) serviceFee / participantCount else 0L
    val participantTax = if (participantCount > 0) taxAmount / participantCount else 0L

    // Hitung total
    val total = subtotal + participantServiceFee + participantTax

    return Tuple4(subtotal, participantServiceFee, participantTax, total)
}