package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavController
import com.example.billbuddy.model.EventData
import com.example.billbuddy.model.Item
import com.example.billbuddy.model.Participant
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.MainViewModel
import com.example.billbuddy.ui.components.AppFilledButton
import com.example.billbuddy.ui.components.AppIconButton
import com.example.billbuddy.ui.components.CommonNavigationBar
import com.example.billbuddy.util.Tuple4

@Composable
fun AssignItemsScreen(
    eventId: String,
    selectedFriendsParam: String, // Daftar teman yang dipilih, dipisahkan koma
    navController: NavController,
    viewModel: MainViewModel
) {
    // Warna sesuai desain
    val backgroundColor = Color(0xFFFFDCDC) // Latar pink
    val buttonColor = Color(0xFFFFB6C1) // Warna tombol pink
    val textColor = Color(0xFF4A4A4A) // Warna teks abu-abu tua
    val divideEvenlyColor = Color(0xFF6A5ACD) // Warna ungu untuk tombol divide evenly saat aktif

    // Ambil data event dari ViewModel
    val eventData by viewModel.eventData.observeAsState()
    val error by viewModel.error.observeAsState()

    // Ambil detail event saat layar dimuat
    LaunchedEffect(eventId) {
        viewModel.getEventDetails(eventId)
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
            val totalItems = event.items.size
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
                .background(backgroundColor)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header dengan tombol close dan tombol divide evenly
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Item untuk ${currentMember?.name ?: "Member"}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.weight(1f))
                // Tombol Divide Evenly (bulatan)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (divideEvenly.value) divideEvenlyColor else Color.Gray,
                            shape = CircleShape
                        )
                        .clickable { divideEvenly.value = !divideEvenly.value },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "%",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Tombol Close
                AppIconButton(
                    onClick = { navController.popBackStack() },
                    icon = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daftar semua member
            localEventData?.let { event ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    allMembers.forEachIndexed { index, member ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    currentMemberIndex.value = index
                                    divideEvenly.value = false // Reset divide evenly saat ganti member
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (currentMemberIndex.value == index) buttonColor else Color.Gray,
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "👤", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = member.name,
                                fontSize = 12.sp,
                                color = textColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Daftar item
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
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
                                enabled = !divideEvenly.value, // Nonaktifkan jika divide evenly aktif
                                colors = CheckboxDefaults.colors(
                                    checkedColor = buttonColor,
                                    uncheckedColor = textColor
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = item.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Text(
                                    text = "Rp ${item.unitPrice}",
                                    fontSize = 14.sp,
                                    color = textColor
                                )
                            }
                            Text(
                                text = "${item.quantity}x",
                                fontSize = 16.sp,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Rp ${item.totalPrice}",
                                fontSize = 16.sp,
                                color = textColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subtotal, Service Fee, Tax, Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SUBTOTAL",
                        fontSize = 16.sp,
                        color = textColor
                    )
                    Text(
                        text = "Rp $subtotal",
                        fontSize = 16.sp,
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Service Fee",
                        fontSize = 16.sp,
                        color = textColor
                    )
                    Text(
                        text = "Rp $participantServiceFee",
                        fontSize = 16.sp,
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tax",
                        fontSize = 16.sp,
                        color = textColor
                    )
                    Text(
                        text = "Rp $participantTax",
                        fontSize = 16.sp,
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                            text = "(${allMembers.size})",
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
            } ?: Text(text = "Memuat detail event...")

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Next
            AppFilledButton(
                onClick = {
                    if (eventId.isNotEmpty()) {
                        // Simpan semua perubahan untuk semua member
                        allMembers.forEach { member ->
                            val itemsAssigned = selectedItemsForMembers[member.id]?.filter { it.value }?.keys?.toList() ?: emptyList()
                            if (member.isExisting) {
                                // Update participant yang sudah ada
                                viewModel.updateParticipantItems(eventId, member.id, itemsAssigned)
                            } else {
                                // Tambah teman baru sebagai participant
                                viewModel.addParticipant(eventId, member.name, itemsAssigned)
                            }
                        }
                        // Navigasi ke ParticipantScreen
                        navController.navigate(NavRoutes.Participant.createRoute(eventId))
                    } else {
                        // Log error untuk debugging
                        println("Error: eventId is empty")
                    }
                },
                text = "Next",
                containerColor = buttonColor,
                textColor = Color.White,
                modifier = Modifier.fillMaxWidth()
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