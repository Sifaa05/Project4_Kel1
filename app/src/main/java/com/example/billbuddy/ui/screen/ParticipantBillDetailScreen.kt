package com.example.billbuddy.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.billbuddy.ui.components.AppBranding
import com.example.billbuddy.ui.components.AppFilledButton
import com.example.billbuddy.ui.components.CommonNavigationBar
import com.example.billbuddy.ui.components.HomeHeader
import com.example.billbuddy.ui.theme.*
import com.example.billbuddy.ui.viewModel.MainViewModel

@Composable
fun ParticipantBillDetailScreen(
    eventId: String,
    participantId: String,
    navController: NavController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val eventData by viewModel.eventData.observeAsState()
    var isLoading by remember { mutableStateOf(true) }
    val error by viewModel.error.observeAsState()

    // Ambil detail event dari database
    LaunchedEffect(eventId) {
        viewModel.getEventDetails(eventId)
        isLoading = false
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.uploadPaymentProof(eventId, participantId, it)
            Toast.makeText(context, "Uploading payment proof...", Toast.LENGTH_SHORT).show()
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Header
                HomeHeader(
                    navController = navController,
                    viewModel = viewModel,
                    showBackButton = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Branding
                AppBranding(isHorizontal = true)

                Spacer(modifier = Modifier.height(16.dp))

                // Judul
                Text(
                    text = "Bill Details",
                    style = MaterialTheme.typography.displayMedium,
                    color = PinkButtonStroke
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Konten utama
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp)

                    )
                            //.align(Alignment.CenterHorizontally)
                } else if (error != null) {
                    Text(
                        text = "Error: $error",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
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

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBackground)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
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
                                            tint = PinkButtonStroke,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = p.name,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                                            color = DarkGreyText,
                                            fontFamily = KhulaExtrabold
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
                                                tint = PinkButtonStroke,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Items",
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                color = DarkGreyText,
                                                fontFamily = KadwaFontFamily
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f, fill = false)
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
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                                                        color = DarkGreyText,
                                                        fontFamily = RobotoFontFamily
                                                    )
                                                    Row {
                                                        Text(
                                                            text = "${item.quantity}x",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = DarkGreyText,
                                                            fontFamily = RobotoFontFamily
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = "Rp ${item.totalPrice / (itemSelectionCount[item.itemId] ?: 1)}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                                            color = DarkGreyText,
                                                            fontFamily = RobotoFontFamily
                                                        )
                                                    }
                                                }
                                                Divider(
                                                    color = BlackText.copy(alpha = 0.2f),
                                                    thickness = 1.dp,
                                                    modifier = Modifier.padding(vertical = 4.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = "No items selected",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DarkGreyText.copy(alpha = 0.6f),
                                            fontFamily = RobotoFontFamily,
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Divider
                                    Divider(
                                        color = BlackText.copy(alpha = 0.2f),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Rincian tagihan
                                    Text(
                                        text = "Bill Details",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                        color = PinkButtonStroke,
                                        fontFamily = KadwaFontFamily
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Subtotal",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DarkGreyText,
                                            fontFamily = RobotoFontFamily
                                        )
                                        Text(
                                            text = "Rp $subtotal",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                            color = DarkGreyText,
                                            fontFamily = RobotoFontFamily
                                        )
                                    }
                                    Divider(
                                        color = BlackText.copy(alpha = 0.2f),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "+ Tax",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DarkGreyText,
                                            fontFamily = RobotoFontFamily
                                        )
                                        Text(
                                            text = "Rp $participantTax",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                            color = DarkGreyText,
                                            fontFamily = RobotoFontFamily
                                        )
                                    }
                                    Divider(
                                        color = BlackText.copy(alpha = 0.2f),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "+ Service Fee",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DarkGreyText,
                                            fontFamily = RobotoFontFamily
                                        )
                                        Text(
                                            text = "Rp $participantServiceFee",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                            color = DarkGreyText,
                                            fontFamily = RobotoFontFamily
                                        )
                                    }
                                    Divider(
                                        color = BlackText.copy(alpha = 0.2f),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .background(PinkBackground.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Total Bill",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                                            color = PinkButtonStroke,
                                            fontFamily = KhulaExtrabold
                                        )
                                        Text(
                                            text = "Rp $total",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                                            color = PinkButtonStroke,
                                            fontFamily = KhulaExtrabold
                                        )
                                    }

                                    // Penanganan kondisi tombol upload bukti pembayaran
                                    if (!p.paid && p.userId != null) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        AppFilledButton(
                                            onClick = { launcher.launch("image/*") },
                                            text = "Upload Payment Proof",
                                            containerColor = PinkButton,
                                            textColor = White,
                                            icon = Icons.Default.CameraAlt,
                                            iconTint = White,
                                            modifier = Modifier.fillMaxWidth(),
                                            height = 60.dp,
                                            fontSize = 20,
                                            cornerRadius = 60.dp,
                                            borderWidth = 2.dp,
                                            borderColor = PinkButtonStroke
                                        )

                                        // Tampilkan foto bukti pembayaran jika ada
                                        p.paymentProofUrl?.let { url ->
                                            Spacer(modifier = Modifier.height(16.dp))
                                            AsyncImage(
                                                model = url,
                                                contentDescription = "Payment Proof",
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(200.dp)
                                                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp))
                                                    .background(CardBackground, RoundedCornerShape(8.dp))
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = if (p.paid) "Payment already confirmed" else "User ID not available",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DarkGreyText.copy(alpha = 0.6f),
                                            fontFamily = RobotoFontFamily,
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp)) // Tambahan padding bawah
                                }
                            }
                        } ?: Text(
                            text = "Participant not found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}