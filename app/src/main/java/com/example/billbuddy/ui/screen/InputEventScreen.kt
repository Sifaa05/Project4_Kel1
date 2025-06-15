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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billbuddy.repository.SplitBillRepository
import com.example.billbuddy.repository.UserRepository
import com.example.billbuddy.data.Item
import com.example.billbuddy.data.Participant
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.components.AppFilledButton
import com.example.billbuddy.ui.components.AppIconButton
import com.example.billbuddy.ui.components.CommonNavigationBar
import com.example.billbuddy.ui.components.HomeHeader
import com.example.billbuddy.ui.theme.*
import com.example.billbuddy.ui.viewModel.MainViewModel
import com.example.billbuddy.util.addOrUpdateItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun InputEventScreen(
    navController: NavController,
    repository: SplitBillRepository,
    viewModel: MainViewModel,
    scannedBillDataJson: String? = null
) {
    val userRepository = remember { UserRepository() }
    var creatorName by remember { mutableStateOf("") }
    var creatorId by remember { mutableStateOf("") }
    var eventName by remember { mutableStateOf("") }
    var itemName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf(1) }
    var unitPrice by remember { mutableStateOf("") }
    var serviceFee by remember { mutableStateOf("") }
    var tax by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    val items = remember { mutableStateListOf<Item>() }
    var isEditing by remember { mutableStateOf(false) }
    var editingItemId by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        userRepository.getUserProfile(
            onSuccess = { user ->
                creatorName = user.name ?: "Anonymous"
                creatorId = user.userId ?: "Unknown"
                isLoading = false
            },
            onFailure = { exception ->
                println("Error fetching user profile: ${exception.message}")
                creatorName = "Anonymous"
                creatorId = "Unknown"
                isLoading = false
            }
        )
    }

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

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            snackbarMessage = null
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeHeader(
                navController = navController,
                viewModel = viewModel,
                showBackButton = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Input Bill",
                style = MaterialTheme.typography.displayLarge,
                color = PinkButtonStroke,
                fontFamily = KhulaExtrabold
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                Text(
                    text = "Creator Name: $creatorName",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkGreyText,
                    fontFamily = RobotoFontFamily,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Text(
                    text = "Creator ID: $creatorId",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkGreyText,
                    fontFamily = RobotoFontFamily,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = { Text("Event Name", style = MaterialTheme.typography.labelSmall, fontFamily = RobotoFontFamily) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TextFieldBackground, RoundedCornerShape(50.dp))
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(50.dp))
                        .padding(vertical = 0.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = TextFieldBackground,
                        unfocusedContainerColor = TextFieldBackground,
                        focusedIndicatorColor = PinkButtonStroke,
                        unfocusedIndicatorColor = DarkGreyText,
                        focusedLabelColor = PinkButtonStroke,
                        unfocusedLabelColor = DarkGreyText
                    )
                )
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item Name", style = MaterialTheme.typography.labelSmall, fontFamily = RobotoFontFamily) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TextFieldBackground, RoundedCornerShape(50.dp))
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(50.dp))
                        .padding(vertical = 0.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = TextFieldBackground,
                        unfocusedContainerColor = TextFieldBackground,
                        focusedIndicatorColor = PinkButtonStroke,
                        unfocusedIndicatorColor = DarkGreyText,
                        focusedLabelColor = PinkButtonStroke,
                        unfocusedLabelColor = DarkGreyText
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
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkGreyText,
                        fontFamily = RobotoFontFamily
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            icon = Icons.Default.Remove,
                            contentDescription = "Decrease Quantity",
                            tint = White,
                            modifier = Modifier
                                .background(PinkButton, shape = RoundedCornerShape(20.dp))
                                .size(32.dp)
                        )
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkGreyText,
                            fontFamily = RobotoFontFamily,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        AppIconButton(
                            onClick = { quantity++ },
                            icon = Icons.Default.Add,
                            contentDescription = "Increase Quantity",
                            tint = White,
                            modifier = Modifier
                                .background(PinkButton, shape = RoundedCornerShape(20.dp))
                                .size(32.dp)
                        )
                    }
                }
                OutlinedTextField(
                    value = unitPrice,
                    onValueChange = { unitPrice = it },
                    label = { Text("Unit Price", style = MaterialTheme.typography.labelSmall, fontFamily = RobotoFontFamily) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TextFieldBackground, RoundedCornerShape(50.dp))
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(50.dp))
                        .padding(vertical = 0.dp),
                    shape = RoundedCornerShape(40.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = TextFieldBackground,
                        unfocusedContainerColor = TextFieldBackground,
                        focusedIndicatorColor = PinkButtonStroke,
                        unfocusedIndicatorColor = DarkGreyText,
                        focusedLabelColor = PinkButtonStroke,
                        unfocusedLabelColor = DarkGreyText
                    )
                )
                OutlinedTextField(
                    value = serviceFee,
                    onValueChange = { serviceFee = it },
                    label = { Text("Service Fee", style = MaterialTheme.typography.labelSmall, fontFamily = RobotoFontFamily) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TextFieldBackground, RoundedCornerShape(50.dp))
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(50.dp))
                        .padding(vertical = 0.dp),
                    shape = RoundedCornerShape(40.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = TextFieldBackground,
                        unfocusedContainerColor = TextFieldBackground,
                        focusedIndicatorColor = PinkButtonStroke,
                        unfocusedIndicatorColor = DarkGreyText,
                        focusedLabelColor = PinkButtonStroke,
                        unfocusedLabelColor = DarkGreyText
                    )
                )
                OutlinedTextField(
                    value = tax,
                    onValueChange = { tax = it },
                    label = { Text("Tax", style = MaterialTheme.typography.labelSmall, fontFamily = RobotoFontFamily) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TextFieldBackground, RoundedCornerShape(50.dp))
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(50.dp))
                        .padding(vertical = 0.dp),
                    shape = RoundedCornerShape(40.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = TextFieldBackground,
                        unfocusedContainerColor = TextFieldBackground,
                        focusedIndicatorColor = PinkButtonStroke,
                        unfocusedIndicatorColor = DarkGreyText,
                        focusedLabelColor = PinkButtonStroke,
                        unfocusedLabelColor = DarkGreyText
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (isEditing) {
                        AppFilledButton(
                            onClick = {
                                isEditing = false
                                editingItemId = null
                                itemName = ""
                                quantity = 1
                                unitPrice = ""
                            },
                            text = "",
                            containerColor = PinkTua,
                            textColor = White,
                            icon = Icons.Default.Cancel,
                            iconTint = White,
                            modifier = Modifier
                                .size(40.dp)
                                .shadow(elevation = 10.dp, shape = RoundedCornerShape(20.dp)),
                            height = 40.dp,
                            fontSize = 12,
                            cornerRadius = 20.dp,
                            borderWidth = 2.dp,
                            borderColor = PinkButtonStroke
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    AppFilledButton(
                        onClick = {
                            val result = addOrUpdateItem(
                                items = items,
                                itemName = itemName,
                                quantity = quantity,
                                unitPrice = unitPrice,
                                isEditing = isEditing,
                                editingItemId = editingItemId
                            )
                            if (result.success) {
                                items.clear()
                                items.addAll(result.updatedItems)
                                isEditing = false
                                editingItemId = null
                                itemName = ""
                                quantity = 1
                                unitPrice = ""
                            }
                            snackbarMessage = result.message
                        },
                        text = "",
                        containerColor = PinkButton,
                        textColor = White,
                        icon = Icons.Default.Add,
                        iconTint = White,
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(elevation = 1.dp, shape = RoundedCornerShape(20.dp)),
                        height = 40.dp,
                        fontSize = 12,
                        cornerRadius = 20.dp,
                        borderWidth = 2.dp,
                        borderColor = PinkButtonStroke
                    )
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .padding(vertical = 8.dp)
                        .shadow(elevation = 10.dp, shape = RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        if (items.isEmpty()) {
                            item {
                                Text(
                                    text = "Belum ada item ditambahkan",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkGreyText,
                                    fontFamily = RobotoFontFamily,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            items(items) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${item.name} (x${item.quantity})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkGreyText,
                                        fontFamily = RobotoFontFamily,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Rp ${item.totalPrice}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkGreyText,
                                        fontFamily = RobotoFontFamily
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AppFilledButton(
                                        onClick = {
                                            isEditing = true
                                            editingItemId = item.itemId
                                            itemName = item.name
                                            quantity = item.quantity
                                            unitPrice = item.unitPrice.toString()
                                        },
                                        text = "",
                                        containerColor = PinkButton,
                                        textColor = White,
                                        icon = Icons.Default.Edit,
                                        iconTint = White,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .shadow(elevation = 10.dp, shape = RoundedCornerShape(20.dp)),
                                        height = 32.dp,
                                        fontSize = 12,
                                        cornerRadius = 20.dp,
                                        borderWidth = 2.dp,
                                        borderColor = PinkButtonStroke
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AppFilledButton(
                                        onClick = {
                                            items.removeIf { it.itemId == item.itemId }
                                            if (isEditing && editingItemId == item.itemId) {
                                                isEditing = false
                                                editingItemId = null
                                                itemName = ""
                                                quantity = 1
                                                unitPrice = ""
                                            }
                                            snackbarMessage = "Item ${item.name} deleted"
                                        },
                                        text = "",
                                        containerColor = PinkTua,
                                        textColor = White,
                                        icon = Icons.Default.Delete,
                                        iconTint = White,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .shadow(elevation = 10.dp, shape = RoundedCornerShape(20.dp)),
                                        height = 32.dp,
                                        fontSize = 12,
                                        cornerRadius = 20.dp,
                                        borderWidth = 2.dp,
                                        borderColor = PinkButtonStroke
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
                }
                Spacer(modifier = Modifier.height(16.dp))
                AppFilledButton(
                    onClick = {
                        if (creatorName.isNotEmpty() && creatorId.isNotEmpty() && eventName.isNotEmpty()) {
                            if (!isEditing) {
                                val result = addOrUpdateItem(
                                    items = items,
                                    itemName = itemName,
                                    quantity = quantity,
                                    unitPrice = unitPrice,
                                    isEditing = isEditing,
                                    editingItemId = editingItemId
                                )
                                if (result.success) {
                                    items.clear()
                                    items.addAll(result.updatedItems)
                                    itemName = ""
                                    quantity = 1
                                    unitPrice = ""
                                }
                                snackbarMessage = result.message
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
                    containerColor = PinkButton,
                    textColor = White,
                    icon = Icons.Default.ArrowForward,
                    iconTint = White,
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
}