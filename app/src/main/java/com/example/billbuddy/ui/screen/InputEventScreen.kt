package com.example.billbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billbuddy.data.SplitBillRepository
import com.example.billbuddy.model.Item
import com.example.billbuddy.model.Participant
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.components.AppFilledButton
import com.example.billbuddy.ui.components.AppIconButton
import com.example.billbuddy.ui.components.CommonNavigationBar

@Composable
fun InputEventScreen(
    navController: NavController,
    repository: SplitBillRepository
) {
    // State variables for form inputs
    var creatorName by remember { mutableStateOf("") }
    var creatorId by remember { mutableStateOf("") }
    var eventName by remember { mutableStateOf("") }
    var itemName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    var serviceFee by remember { mutableStateOf("") }
    var tax by remember { mutableStateOf("") }

    // List to store multiple items
    val items = remember { mutableStateListOf<Item>() }

    // State untuk Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // State untuk pesan Snackbar
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // Tampilkan Snackbar ketika snackbarMessage berubah
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            snackbarMessage = null // Reset setelah ditampilkan
        }
    }

    // Colors matching the UI
    val backgroundColor = Color(0xFFFFDCDC) // Light pink background
    val buttonColor = Color(0xFFFFB6C1) // Slightly darker pink for buttons
    val textColor = Color(0xFF4A4A4A) // Dark gray for text

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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back button and title
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

            // Creator Name
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

            // Creator ID
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

            // Event Name
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

            // Item Name
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

            // Quantity
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
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

            // Unit Price
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

            // Service Fee
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

            // Tax
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

            // Add Item Button
            AppIconButton(
                onClick = {
                    if (itemName.isNotEmpty() && quantity.isNotEmpty() && unitPrice.isNotEmpty()) {
                        val quantityValue = quantity.toIntOrNull() ?: 0
                        val unitPriceValue = unitPrice.toLongOrNull() ?: 0
                        val totalPriceValue = unitPriceValue * quantityValue
                        items.add(
                            Item(
                                itemId = "",
                                name = itemName,
                                quantity = quantityValue,
                                unitPrice = unitPriceValue,
                                totalPrice = totalPriceValue
                            )
                        )
                        itemName = ""
                        quantity = ""
                        unitPrice = ""
                    } else {
                        snackbarMessage = "Please fill in all item fields"
                    }
                },
                icon = Icons.Default.Add,
                contentDescription = "Add Item",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(vertical = 8.dp)
                    .background(buttonColor, shape = RoundedCornerShape(50))
                    .size(40.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Make Bill Button
            AppFilledButton(
                onClick = {
                    if (creatorName.isNotEmpty() && creatorId.isNotEmpty() && eventName.isNotEmpty()) {
                        if (itemName.isNotEmpty() && quantity.isNotEmpty() && unitPrice.isNotEmpty()) {
                            val quantityValue = quantity.toIntOrNull() ?: 0
                            val unitPriceValue = unitPrice.toLongOrNull() ?: 0
                            val totalPriceValue = unitPriceValue * quantityValue
                            items.add(
                                Item(
                                    itemId = "",
                                    name = itemName,
                                    quantity = quantityValue,
                                    unitPrice = unitPriceValue,
                                    totalPrice = totalPriceValue
                                )
                            )
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
        }
    }
}