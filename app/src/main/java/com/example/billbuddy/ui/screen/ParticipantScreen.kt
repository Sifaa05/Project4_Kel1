package com.example.billbuddy.ui.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billbuddy.data.EventData
import com.example.billbuddy.data.Participant
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.components.*
import com.example.billbuddy.ui.theme.*
import com.example.billbuddy.ui.viewModel.MainViewModel
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ParticipantBill(
    val subtotal: Long,
    val serviceFee: Long,
    val tax: Long,
    val total: Long
)

@Composable
fun ParticipantScreen(
    eventId: String,
    viewModel: MainViewModel,
    navController: NavController
) {
    Log.d("ParticipantScreen", "Event ID received by Composable (from nav): $eventId")

    // Ambil data dari ViewModel
    val eventData by viewModel.eventData.observeAsState()
    val error by viewModel.error.observeAsState()
    var isLoading by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val clipboardManager = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }



    // Ambil detail event dari database
    LaunchedEffect(eventId) {
        Log.d("ParticipantScreen", "LaunchedEffect triggered for Event ID: $eventId")
        viewModel.getEventDetails(eventId)
        isLoading = false
        isLoading = false
        // === DEBUGGING: Log eventData setelah dimuat ===
        eventData?.let {
            Log.d("ParticipantScreen", "EventData loaded: Event ID from data = ${it.eventId}, Name = ${it.eventName}")
        } ?: Log.d("ParticipantScreen", "EventData is null after loading.")
    }


    suspend fun generateAndCopyDynamicLink(eventId: String, snackbarHostState: SnackbarHostState) {
        Log.d("DynamicLink", "Attempting to generate link for Event ID: $eventId")
        try {
            if (eventId.isEmpty()) {
                snackbarHostState.showSnackbar("Event ID is empty, cannot generate link.")
                Log.e("DynamicLink", "Attempted to generate link with empty Event ID.")
                return
            }

            val baseLink = Uri.parse("https://billbuddy.page.link/sharedbill/$eventId")
            Log.d("DynamicLink", "Base Link constructed: $baseLink")
            val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(baseLink)
                .setDomainUriPrefix("https://billbuddy.page.link")
                .setAndroidParameters(
                    DynamicLink.AndroidParameters.Builder("com.example.billbuddy")
                        .setMinimumVersion(1)
                        .build()
                )
                .setIosParameters(
                    DynamicLink.IosParameters.Builder("com.example.billbuddy")
                        .setMinimumVersion("1.0")
                        .build()
                )
                .setSocialMetaTagParameters(
                    DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle("BillBuddy Shared Bill")
                        .setDescription("Join the bill sharing event")
                        .build()
                )

            // Coba generate short link
            val shortLinkResult = try {
                Log.d("DynamicLink", "Attempting to build short dynamic link...")
                dynamicLink.buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT).await()
            } catch (e: Exception) {
                Log.e("DynamicLink", "Short link generation failed: ${e.message}", e)
                snackbarHostState.showSnackbar("Short link generation failed: ${e.message}. Trying long link.")
                null
            }

            val linkToCopy = if (shortLinkResult?.shortLink != null) {
                shortLinkResult.shortLink
            } else if (shortLinkResult?.previewLink != null) {
                shortLinkResult.previewLink
            } else {
                Log.d("DynamicLink", "Falling back to long dynamic link.")
                dynamicLink.buildDynamicLink().uri // Fallback ke long link
            }

            if (linkToCopy != null) {
                Log.i("DynamicLink", "Final link to copy: $linkToCopy")
                clipboardManager.setPrimaryClip(ClipData.newPlainText("Bill Link", linkToCopy.toString()))
                snackbarHostState.showSnackbar(
                    if (shortLinkResult?.shortLink != null) "Short link copied to clipboard!"
                    else "Long link copied to clipboard (short link failed)"
                )
            } else {
                snackbarHostState.showSnackbar("Failed to generate any valid link")
                Log.e("DynamicLink", "No valid link could be generated.")
            }
        } catch (e: Exception) {
                Log.e("DynamicLink", "Error generating Dynamic Link: ${e.message}, StackTrace: ${e.stackTraceToString()}", e)
            val errorMessage = when (e) {
                is com.google.firebase.FirebaseException -> "Firebase error: ${e.message}"
                else -> "Failed to generate link: ${e.message}"
            }
            snackbarHostState.showSnackbar(errorMessage)
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                text = "Participants",
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
                    Log.d("ParticipantScreen", "Displaying participants for Event ID: ${event.eventId}")

                    // Jumlah peserta
                    Text(
                        text = "Participants (${event.participants.size}):",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkGreyText,
                        fontFamily = KhulaRegular,
                        modifier = Modifier.align(Alignment.Start)
                    )
//            } else {
//                eventData?.let { event ->
//                    // Jumlah peserta
//                    Text(
//                        text = "Participants (${event.participants.size}):",
//                        style = MaterialTheme.typography.bodyLarge,
//                        color = DarkGreyText,
//                        fontFamily = KadwaFontFamily,
//                        modifier = Modifier.align(Alignment.Start)
//                    )

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
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row {
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
                                                text = "${participant.name}", //(Paid: ${participant.paid})",
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = DarkGreyText,
                                                fontFamily = KadwaFontFamily
                                            )
                                        }
                                        Row {
                                            AppFilledButton(
                                                onClick = {
                                                    viewModel.updatePaymentStatus(
                                                        event.eventId,
                                                        participant.id,
                                                        !participant.paid
                                                    )
                                                },
                                                text = if (participant.paid) "Unpaid" else "Paid",
                                                containerColor = if (participant.paid) PinkTua else PinkButton,
                                                textColor = White,
                                                icon = Icons.Default.Check,
                                                iconTint = White,
                                                modifier = Modifier
                                                    .height(50.dp)
                                                    .widthIn(min = 0.dp)
                                                    .padding(end = 3.dp),
                                                fontSize = 12,
                                                cornerRadius = 20.dp,
                                                borderWidth = 2.dp,
                                                borderColor = PinkButtonStroke
                                            )
                                            AppFilledButton(
                                                onClick = {
                                                    navController.navigate(
                                                        NavRoutes.ParticipantBillDetail.createRoute(
                                                            eventId,
                                                            participant.id
                                                        )
                                                    )
                                                },
                                                text = "Detail",
                                                containerColor = PinkButton,
                                                textColor = White,
                                                icon = Icons.Default.Receipt,
                                                iconTint = White,
                                                modifier = Modifier
                                                    .height(50.dp)
                                                    .widthIn(min = 0.dp),
                                                fontSize = 12,
                                                cornerRadius = 20.dp,
                                                borderWidth = 2.dp,
                                                borderColor = PinkButtonStroke
                                            )
                                        }
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
                                            fontFamily = RobotoFontFamily
                                        )
                                        Text(
                                            text = "Rp ${bill.subtotal}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DarkGreyText,
                                            fontFamily = RobotoFontFamily
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
                                            fontFamily = RobotoFontFamily
                                        )
                                        Text(
                                            text = "Rp ${bill.serviceFee}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DarkGreyText,
                                            fontFamily = RobotoFontFamily
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
                                            fontFamily = RobotoFontFamily
                                        )
                                        Text(
                                            text = "Rp ${bill.tax}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DarkGreyText,
                                            fontFamily = RobotoFontFamily
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
                                            fontFamily = KhulaExtrabold
                                        )
                                        Text(
                                            text = "Rp ${bill.total}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = PinkButtonStroke,
                                            fontFamily = KhulaExtrabold
                                        )
                                    }
                                }
                            }
                        }
                        // Tombol Add Buddy dan Generate Link di bawah daftar peserta
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                AppFilledButton(
                                    onClick = { navController.navigate(NavRoutes.AddBuddy.createRoute(eventId)) },
                                    text = "Add Buddy",
                                    containerColor = TextFieldBackground,
                                    textColor = White,
                                    icon = Icons.Default.Add,
                                    iconTint = White,
                                    modifier = Modifier.weight(1f),
                                    height = 60.dp,
                                    fontSize = 20,
                                    cornerRadius = 60.dp,
                                    borderWidth = 2.dp,
                                    borderColor = PinkButtonStroke
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                AppFilledButton(
                                    onClick = {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            val idToUseForLink = eventData?.eventId ?: eventId
                                            if (idToUseForLink.isNotEmpty()) {
                                                generateAndCopyDynamicLink(idToUseForLink, snackbarHostState)
                                            } else {
                                                snackbarHostState.showSnackbar("Event ID not available to generate link.")
                                                Log.e("ParticipantScreen", "Cannot generate link: Event ID is empty or null.")
                                            }
                                        }
                                    },
                                    text = "Generate Link",
                                    containerColor = TextFieldBackground,
                                    textColor = White,
                                    icon = Icons.Default.Link,
                                    iconTint = White,
                                    modifier = Modifier.weight(1f),
                                    height = 60.dp,
                                    fontSize = 20,
                                    cornerRadius = 60.dp,
                                    borderWidth = 2.dp,
                                    borderColor = PinkButtonStroke
                                )
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
}

@Composable
fun calculateParticipantBill(
    participant: Participant,
    event: EventData,
    itemSelectionCount: Map<String, Int>,
    participantCount: Int
): ParticipantBill {
    // Hitung subtotal berdasarkan item yang dipilih oleh participant
    val subtotal = event.items
        .filter { participant.itemsAssigned?.contains(it.itemId) == true }
        .sumOf { item ->
            val participantsForItem = itemSelectionCount[item.itemId] ?: 1
            (item.totalPrice / participantsForItem).toLong()
        }

    // Hitung service fee dan tax berdasarkan jumlah participant
    val serviceFee = if (participantCount > 0) event.serviceFee / participantCount else 0L
    val tax = if (participantCount > 0) event.taxAmount / participantCount else 0L

    // Hitung total
    val total = subtotal + serviceFee + tax

    return ParticipantBill(subtotal, serviceFee, tax, total)
}