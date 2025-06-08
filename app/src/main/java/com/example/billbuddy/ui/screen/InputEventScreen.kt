package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billbuddy.repository.SplitBillRepository
import com.example.billbuddy.data.Item
import com.example.billbuddy.data.Participant
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.components.AppFilledButton
import com.example.billbuddy.ui.components.AppIconButton
import com.example.billbuddy.ui.components.CommonNavigationBar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

@Composable
fun InputEventScreen(
    navController: NavController,
    repository: SplitBillRepository,
    scannedBillDataJson: String? = null
) {
    var creatorName by remember { mutableStateOf("") }
    var creatorId by remember { mutableStateOf("") }
    var eventName by remember { mutableStateOf("") }
    var itemName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf(1) }
    var unitPrice by remember { mutableStateOf("") }
    var serviceFee by remember { mutableStateOf("") }
    var tax by remember { mutableStateOf("") }

    val items = remember {
        mutableStateListOf<Item>()
    }

    // Proses data yang dikirim dari ScanScreen
    LaunchedEffect(scannedBillDataJson) {
        scannedBillDataJson?.let {
            if (it.isNotEmpty()) {
                try {
                    val decodedJson = java.net.URLDecoder.decode(it, "UTF-8")
                    val type = object : TypeToken<ScannedBillData>() {}.type
                    val scannedBillData: ScannedBillData = Gson().fromJson(decodedJson, type)
                    items.addAll(scannedBillData.items)
                    tax = scannedBillData.tax.toString()
                    serviceFee = scannedBillData.serviceFee.toString()
                } catch (e: Exception) {
                    // Jika parsing gagal, biarkan kolom kosong
                }
            }
        }
    }

    var isEditing by remember { mutableStateOf(false) }
    var editingItemId by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            snackbarMessage = null
        }
    }

    val backgroundColor = Color(0xFFFFDCDC)
    val buttonColor = Color(0xFFFFB6C1)
    val textColor = Color(0xFF4A4A4A)

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
                .background(backgroundColor)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Input Bill",
                    fontSize = 24.sp,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = creatorName,
                onValueChange = { creatorName = it },
                label = { Text("Creator Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            OutlinedTextField(
                value = creatorId,
                onValueChange = { creatorId = it },
                label = { Text("Creator ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Event Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Quantity",
                    color = textColor,
                    fontSize = 16.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        icon = Icons.Default.Remove,
                        contentDescription = "Decrease Quantity",
                        tint = Color.White,
                        modifier = Modifier
                            .background(buttonColor, shape = RoundedCornerShape(50))
                            .size(40.dp)
                    )
                    Text(
                        text = quantity.toString(),
                        color = textColor,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    AppIconButton(
                        onClick = { quantity++ },
                        icon = Icons.Default.Add,
                        contentDescription = "Increase Quantity",
                        tint = Color.White,
                        modifier = Modifier
                            .background(buttonColor, shape = RoundedCornerShape(50))
                            .size(40.dp)
                    )
                }
            }

            OutlinedTextField(
                value = unitPrice,
                onValueChange = { unitPrice = it },
                label = { Text("Unit Price") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            OutlinedTextField(
                value = serviceFee,
                onValueChange = { serviceFee = it },
                label = { Text("Service Fee") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            OutlinedTextField(
                value = tax,
                onValueChange = { tax = it },
                label = { Text("Tax") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (isEditing) {
                    AppIconButton(
                        onClick = {
                            isEditing = false
                            editingItemId = null
                            itemName = ""
                            quantity = 1
                            unitPrice = ""
                        },
                        icon = Icons.Default.Cancel,
                        contentDescription = "Cancel Edit",
                        tint = Color.White,
                        modifier = Modifier
                            .background(Color.Red, shape = RoundedCornerShape(50))
                            .size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                AppIconButton(
                    onClick = {
                        if (itemName.isNotEmpty() && quantity > 0 && unitPrice.isNotEmpty()) {
                            val unitPriceValue = unitPrice.toLongOrNull() ?: 0
                            val totalPriceValue = unitPriceValue * quantity
                            if (isEditing && editingItemId != null) {
                                val index = items.indexOfFirst { it.itemId == editingItemId }
                                if (index != -1) {
                                    items[index] = Item(
                                        itemId = editingItemId!!,
                                        name = itemName,
                                        quantity = quantity,
                                        unitPrice = unitPriceValue,
                                        totalPrice = totalPriceValue
                                    )
                                }
                                isEditing = false
                                editingItemId = null
                            } else {
                                items.add(
                                    Item(
                                        itemId = UUID.randomUUID().toString(),
                                        name = itemName,
                                        quantity = quantity,
                                        unitPrice = unitPriceValue,
                                        totalPrice = totalPriceValue
                                    )
                                )
                            }
                            itemName = ""
                            quantity = 1
                            unitPrice = ""
                        } else {
                            snackbarMessage = "Please fill in all item fields"
                        }
                    },
                    icon = Icons.Default.Add,
                    contentDescription = if (isEditing) "Save Changes" else "Add Item",
                    tint = Color.White,
                    modifier = Modifier
                        .background(buttonColor, shape = RoundedCornerShape(50))
                        .size(40.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .padding(vertical = 8.dp)
            ) {
                if (items.isEmpty()) {
                    item {
                        Text(
                            text = "Belum ada item ditambahkan",
                            color = textColor,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(items) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${item.name} (x${item.quantity})",
                                    color = textColor,
                                    fontSize = 16.sp
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Rp ${item.totalPrice}",
                                        color = textColor,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AppIconButton(
                                        onClick = {
                                            isEditing = true
                                            editingItemId = item.itemId
                                            itemName = item.name
                                            quantity = item.quantity
                                            unitPrice = item.unitPrice.toString()
                                        },
                                        icon = Icons.Default.Edit,
                                        contentDescription = "Edit Item",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .background(Color(0xFF4CAF50), shape = RoundedCornerShape(50))
                                            .size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AppFilledButton(
                onClick = {
                    if (creatorName.isNotEmpty() && creatorId.isNotEmpty() && eventName.isNotEmpty()) {
                        if (itemName.isNotEmpty() && quantity > 0 && unitPrice.isNotEmpty() && !isEditing) {
                            val unitPriceValue = unitPrice.toLongOrNull() ?: 0
                            val totalPriceValue = unitPriceValue * quantity
                            items.add(
                                Item(
                                    itemId = UUID.randomUUID().toString(),
                                    name = itemName,
                                    quantity = quantity,
                                    unitPrice = unitPriceValue,
                                    totalPrice = totalPriceValue
                                )
                            )
                            itemName = ""
                            quantity = 1
                            unitPrice = ""
                        }
                        if (items.isNotEmpty()) {
                            repository.createSplitEvent(
                                creatorId = creatorId,
                                creatorName = creatorName,
                                eventName = eventName,
                                items = items,
                                participants = listOf(
                                    Participant(
                                        id = creatorId,
                                        name = creatorName,
                                        userId = creatorId,
                                        amount = 0,
                                        paid = false,
                                        isCreator = true
                                    )
                                ),
                                splitType = "event",
                                taxAmount = tax.toLongOrNull() ?: 0,
                                serviceFee = serviceFee.toLongOrNull() ?: 0,
                                onSuccess = { eventId ->
                                    if (eventId.isNotEmpty()) {
                                        navController.navigate(NavRoutes.EventDetail.createRoute(eventId))
                                    } else {
                                        snackbarMessage = "Failed to create event: Invalid event ID"
                                    }
                                },
                                onFailure = { exception ->
                                    snackbarMessage = "Failed to create event: ${exception.message}"
                                }
                            )
                        } else {
                            snackbarMessage = "Please add at least 1 item"
                        }
                    } else {
                        snackbarMessage = "Please fill in all required fields (Creator Name, Creator ID, Event Name)"
                    }
                },
                text = "Make Bill!",
                containerColor = buttonColor,
                textColor = Color.White,
                icon = Icons.Default.ArrowForward,
                iconTint = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}