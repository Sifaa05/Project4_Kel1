package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billbuddy.data.EventData
import com.example.billbuddy.data.Participant
import com.example.billbuddy.ui.components.AppBranding
import com.example.billbuddy.ui.components.CommonNavigationBar
import com.example.billbuddy.ui.components.HomeHeader
import com.example.billbuddy.ui.theme.*
import com.example.billbuddy.ui.viewModel.MainViewModel
import com.example.billbuddy.ui.theme.Font
import com.example.billbuddy.ui.theme.FontType

@Composable
fun SharedBillScreen(
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

//    Scaffold(
//        bottomBar = {
//            CommonNavigationBar(
//                navController = navController,
//                selectedScreen = "List"
//            )
//        },
//        modifier = Modifier.fillMaxSize()
//    )
    //{ padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            //.padding(padding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
//            HomeHeader(
//                navController = navController,
//                showBackButton = true
//            )

        Spacer(modifier = Modifier.height(20.dp))

        // Branding
        AppBranding(isHorizontal = true)

        Spacer(modifier = Modifier.height(10.dp))

        // Judul
        Text(
            text = "Shared Bill",
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
        } else if (error != null) {
            Text(
                text = "Error: $error",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            eventData?.let { event ->
                // Jumlah peserta
                Text(
                    text = "Participants (${event.participants.size}):",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkGreyText,
                    fontFamily = Font.getFont(FontType.KADWA_REGULAR),
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
                        val bill = calculateParticipantBill(
                            participant = participant,
                            event = event,
                            itemSelectionCount = itemSelectionCount,
                            participantCount = participantCount
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .shadow(elevation = 10.dp, shape = RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (participant.isCreator) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Creator",
                                            tint = PinkButtonStroke,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = "${participant.name}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkGreyText,
                                        fontFamily = Font.getFont(FontType.KADWA_REGULAR)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Rincian
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Subtotal",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkGreyText,
                                        fontFamily = Font.getFont(FontType.ROBOTO_REGULAR)
                                    )
                                    Text(
                                        text = "Rp ${bill.subtotal}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkGreyText,
                                        fontFamily = Font.getFont(FontType.ROBOTO_REGULAR)
                                    )
                                }
                                Divider(
                                    color = DarkGreyText.copy(alpha = 0.2f),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Service Fee",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkGreyText,
                                        fontFamily = Font.getFont(FontType.ROBOTO_REGULAR)
                                    )
                                    Text(
                                        text = "Rp ${bill.serviceFee}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkGreyText,
                                        fontFamily = Font.getFont(FontType.ROBOTO_REGULAR)
                                    )
                                }
                                Divider(
                                    color = DarkGreyText.copy(alpha = 0.2f),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Tax",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkGreyText,
                                        fontFamily = Font.getFont(FontType.ROBOTO_REGULAR)
                                    )
                                    Text(
                                        text = "Rp ${bill.tax}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkGreyText,
                                        fontFamily = Font.getFont(FontType.ROBOTO_REGULAR)
                                    )
                                }
                                Divider(
                                    color = DarkGreyText.copy(alpha = 0.2f),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(PinkBackground.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Total",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = PinkButtonStroke,
                                        fontFamily = Font.getFont(FontType.KHULA_EXTRABOLD)
                                    )
                                    Text(
                                        text = "Rp ${bill.total}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = PinkButtonStroke,
                                        fontFamily = Font.getFont(FontType.KHULA_EXTRABOLD)
                                    )
                                }
                            }
                        }
                    }
                }
            } ?: Text(
                text = "No participants found",
                style = MaterialTheme.typography.bodyLarge,
                color = DarkGreyText,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}