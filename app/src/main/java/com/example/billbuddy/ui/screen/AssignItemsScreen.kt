package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.components.AppBranding
import com.example.billbuddy.ui.components.AppFilledButton
import com.example.billbuddy.ui.components.CommonNavigationBar
import com.example.billbuddy.ui.components.HomeHeader
import com.example.billbuddy.ui.theme.*
import com.example.billbuddy.ui.viewModel.MainViewModel
import com.example.billbuddy.util.Tuple4

@Composable
fun AssignItemsScreen(
    eventId: String,
    selectedFriendsParam: String, // Daftar teman yang dipilih, dipisahkan koma
    navController: NavController,
    viewModel: MainViewModel
) {
    // Ambil data event dari ViewModel
    val eventData by viewModel.eventData.observeAsState()
    val error by viewModel.error.observeAsState()
    var isLoading by remember { mutableStateOf(true) }

    // Ambil detail event saat layar dimuat
    LaunchedEffect(eventId) {
        viewModel.getEventDetails(eventId)
        isLoading = false
    }

    // Decode daftar teman yang dipilih
    val selectedFriends = selectedFriendsParam.split(",").filter { it.isNotBlank() }

    // Daftar semua member (participant yang sudah ada + teman baru)
    val allMembers = remember { mutableStateListOf<Member>() }
    val selectedItemsForMembers = remember { mutableStateMapOf<String, MutableMap<String, Boolean>>() }

    // Inisialisasi daftar member dan state item
    LaunchedEffect(eventData, selectedFriends) {
        allMembers.clear()
        selectedItemsForMembers.clear()

        // Simpan eventData ke variabel lokal
        val localEventData = eventData ?: return@LaunchedEffect

        // Tambahkan participant yang sudah ada
        localEventData.participants.forEach { participant ->
            allMembers.add(Member(participant.id, participant.name, true))
            val itemsMap = mutableMapOf<String, Boolean>()
            localEventData.items.forEach { item ->
                itemsMap[item.itemId] = participant.itemsAssigned?.contains(item.itemId) ?: false
            }
            selectedItemsForMembers[participant.id] = itemsMap
        }

        // Tambahkan teman baru
        selectedFriends.forEachIndexed { index, friend ->
            val memberId = "new_$index" // ID sementara untuk teman baru
            allMembers.add(Member(memberId, friend, false))
            val itemsMap = mutableMapOf<String, Boolean>()
            localEventData.items.forEach { item ->
                itemsMap[item.itemId] = false // Default: tidak dipilih
            }
            selectedItemsForMembers[memberId] = itemsMap
        }
    }

    // State untuk member yang sedang ditampilkan
    val currentMemberIndex = remember { mutableStateOf(0) }
    val currentMember = if (allMembers.isNotEmpty() && currentMemberIndex.value < allMembers.size) {
        allMembers[currentMemberIndex.value]
    } else {
        null
    }

    // State untuk divide evenly
    val divideEvenly = remember { mutableStateOf(false) }

    // Simpan eventData ke variabel lokal untuk digunakan di UI
    val localEventData = eventData

    // Hitung subtotal, service fee, tax, dan total untuk member saat ini
    val (subtotal, participantServiceFee, participantTax, total) = localEventData?.let { event ->
        val currentItems = selectedItemsForMembers[currentMember?.id] ?: emptyMap()
        val participantCount = allMembers.size // Jumlah total participant
        if (divideEvenly.value) {
            // Jika divide evenly, semua item diassign untuk semua member
            allMembers.forEach { member ->
                val itemsMap = selectedItemsForMembers[member.id] ?: mutableMapOf()
                event.items.forEach { item ->
                    itemsMap[item.itemId] = true
                }
                selectedItemsForMembers[member.id] = itemsMap
            }
            val totalAmount = event.totalAmount
            val serviceFee = event.serviceFee
            val taxAmount = event.taxAmount
            val amountPerPerson = if (participantCount > 0) totalAmount / participantCount else 0L
            val serviceFeePerPerson = if (participantCount > 0) serviceFee / participantCount else 0L
            val taxPerPerson = if (participantCount > 0) taxAmount / participantCount else 0L
            val totalPerPerson = amountPerPerson + serviceFeePerPerson + taxPerPerson
            Tuple4(amountPerPerson, serviceFeePerPerson, taxPerPerson, totalPerPerson)
        } else {
            // Hitung jumlah participant yang memilih setiap item
            val itemSelectionCount = mutableMapOf<String, Int>()
            event.items.forEach { item ->
                val count = selectedItemsForMembers.values.count { it[item.itemId] == true }
                itemSelectionCount[item.itemId] = if (count > 0) count else 1 // Hindari pembagian dengan 0
            }

            // Hitung subtotal berdasarkan totalPrice yang dibagi rata
            val subtotal = event.items
                .filter { currentItems[it.itemId] == true }
                .sumOf { item ->
                    val participantsForItem = itemSelectionCount[item.itemId] ?: 1
                    (item.totalPrice / participantsForItem).toLong()
                }

            // Hitung service fee dan tax berdasarkan jumlah participant
            val serviceFee = event.serviceFee
            val taxAmount = event.taxAmount
            val participantServiceFee = if (participantCount > 0) serviceFee / participantCount else 0L
            val participantTax = if (participantCount > 0) taxAmount / participantCount else 0L
            val total = subtotal + participantServiceFee + participantTax
            Tuple4(subtotal, participantServiceFee, participantTax, total)
        }
    } ?: Tuple4(0L, 0L, 0L, 0L)

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
                text = "Add Items for ${currentMember?.name ?: "Member"}",
                style = MaterialTheme.typography.displayMedium,
                color = PinkButtonStroke,
                fontFamily = KhulaExtrabold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Divide Evenly
            AppFilledButton(
                onClick = { divideEvenly.value = !divideEvenly.value },
                text = "Divide Evenly",
                containerColor = if (divideEvenly.value) PinkTua else DarkGreyText,
                textColor = White,
                icon = Icons.Default.Percent,
                iconTint = White,
                modifier = Modifier
                    .width(160.dp)
                    .height(40.dp),
                fontSize = 16,
                cornerRadius = 20.dp,
                borderWidth = 2.dp,
                borderColor = PinkButtonStroke
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                    fontFamily = RobotoFontFamily,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                localEventData?.let { event ->
                    // Daftar semua member
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allMembers.forEachIndexed { index, member ->
                            Card(
                                modifier = Modifier
                                    .width(80.dp)
                                    .clickable {
                                        currentMemberIndex.value = index
                                        divideEvenly.value = false // Reset divide evenly saat ganti member
                                    }
                                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp)),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (currentMemberIndex.value == index) PinkButton else CardBackground
                                )
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Member ${member.name}",
                                        tint = if (currentMemberIndex.value == index) White else DarkGreyText,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = member.name.take(8) + if (member.name.length > 8) "..." else "",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (currentMemberIndex.value == index) White else DarkGreyText,
                                        fontFamily = RobotoFontFamily
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Daftar item
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .shadow(elevation = 10.dp, shape = RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            items(event.items) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedItemsForMembers[currentMember?.id]?.get(item.itemId) ?: false,
                                        onCheckedChange = { isChecked ->
                                            val itemsMap = selectedItemsForMembers[currentMember?.id] ?: mutableMapOf()
                                            itemsMap[item.itemId] = isChecked
                                            selectedItemsForMembers[currentMember?.id ?: ""] = itemsMap
                                            if (isChecked) {
                                                divideEvenly.value = false // Matikan divide evenly jika kustom
                                            }
                                        },
                                        enabled = !divideEvenly.value,
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = PinkButton,
                                            uncheckedColor = DarkGreyText
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkGreyText,
                                            fontFamily = RobotoFontFamily
                                        )
                                        Text(
                                            text = "Rp ${item.unitPrice}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DarkGreyText,
                                            fontFamily = RobotoFontFamily
                                        )
                                    }
                                    Text(
                                        text = "${item.quantity}x",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkGreyText,
                                        fontFamily = RobotoFontFamily
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Rp ${item.totalPrice}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkGreyText,
                                        fontFamily = RobotoFontFamily
                                    )
                                }
                                Divider(
                                    color = BlackText.copy(alpha = 0.2f),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Subtotal, Service Fee, Tax, Total
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 10.dp, shape = RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Bill Details",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
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
                                    text = "Service Fee",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkGreyText,
                                    fontFamily = RobotoFontFamily
                                )
                                Text(
                                    text = "Rp $participantServiceFee",
                                    style = MaterialTheme.typography.labelSmall,
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
                                    text = "Tax",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkGreyText,
                                    fontFamily = RobotoFontFamily
                                )
                                Text(
                                    text = "Rp $participantTax",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkGreyText,
                                    fontFamily = RobotoFontFamily
                                )
                            }
                            Divider(
                                color = BlackText.copy(alpha = 0.2f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
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
                                        text = "Total",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PinkButtonStroke,
                                        fontFamily = KhulaExtrabold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "(${allMembers.size})",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = DarkGreyText,
                                        fontFamily = RobotoFontFamily
                                    )
                                }
                                Text(
                                    text = "Rp $total",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PinkButtonStroke,
                                    fontFamily = KhulaExtrabold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Next
            AppFilledButton(
                onClick = {
                    if (eventId.isNotEmpty()) {
                        allMembers.forEach { member ->
                            val itemsAssigned = selectedItemsForMembers[member.id]?.filter { it.value }?.keys?.toList() ?: emptyList()
                            if (member.isExisting) {
                                viewModel.updateParticipantItems(eventId, member.id, itemsAssigned)
                            } else {
                                viewModel.addParticipant(eventId, member.name, itemsAssigned)
                            }
                        }
                        navController.navigate(NavRoutes.Participant.createRoute(eventId))
                    } else {
                        println("Error: eventId is empty")
                    }
                },
                text = "Next",
                containerColor = PinkButton,
                textColor = White,
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

// Data class untuk menyimpan informasi member
data class Member(
    val id: String,
    val name: String,
    val isExisting: Boolean // True jika participant sudah ada, False jika teman baru
)