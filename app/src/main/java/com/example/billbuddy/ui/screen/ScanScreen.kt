package com.example.billbuddy.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.navigation.NavController
import com.example.billbuddy.data.Item
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.components.*
import com.example.billbuddy.ui.theme.*
import com.example.billbuddy.ui.viewModel.MainViewModel
import com.google.gson.Gson
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException
import java.util.UUID
import java.util.regex.Pattern

data class ScannedBillData(
    val items: List<Item>,
    val tax: Long,
    val serviceFee: Long
)

@Composable
fun ScanScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var scannedBillData by remember { mutableStateOf<ScannedBillData?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    var cameraButtonScale by remember { mutableStateOf(1f) }
    var galleryButtonScale by remember { mutableStateOf(1f) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { resultBitmap ->
        bitmap = resultBitmap?.let { adjustBitmapOrientation(it, null, context) }
        bitmap?.let {
            isProcessing = true
            processImage(it) { result ->
                isProcessing = false
                scannedBillData = result
                if (result.items.isEmpty()) {
                    showDialog = true
                } else if (validateScannedData(result)) {
                    val billDataJson = Gson().toJson(result)
                    val encodedBillDataJson = java.net.URLEncoder.encode(billDataJson, "UTF-8")
                    navController.navigate(
                        NavRoutes.InputEvent.createRoute(encodedBillDataJson)
                    ) {
                        popUpTo(NavRoutes.Scan.route) { inclusive = true }
                    }
                } else {
                    snackbarMessage = "Data tidak valid. Silakan coba lagi atau masukkan secara manual."
                }
            }
        } ?: run {
            snackbarMessage = "Gagal mengambil gambar."
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)?.let { adjustBitmapOrientation(it, uri, context) }
            bitmap?.let {
                isProcessing = true
                processImage(it) { result ->
                    isProcessing = false
                    scannedBillData = result
                    if (result.items.isEmpty()) {
                        showDialog = true
                    } else if (validateScannedData(result)) {
                        val billDataJson = Gson().toJson(result)
                        val encodedBillDataJson = java.net.URLEncoder.encode(billDataJson, "UTF-8")
                        navController.navigate(
                            NavRoutes.InputEvent.createRoute(encodedBillDataJson)
                        ) {
                            popUpTo(NavRoutes.Scan.route) { inclusive = true }
                        }
                    } else {
                        snackbarMessage = "Data tidak valid. Silakan coba lagi atau masukkan secara manual."
                    }
                }
            }
        } ?: run {
            snackbarMessage = "Gagal memilih gambar dari galeri."
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        } else {
            snackbarMessage = "Izin kamera ditolak. Silakan aktifkan di pengaturan atau pilih gambar dari galeri."
        }
    }

    if (showDialog && scannedBillData != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Warning",
                    style = MaterialTheme.typography.displayMedium,
                    color = DarkGreyText
                )
            },
            text = {
                Text(
                    text = "Tidak ada item terdeteksi dari gambar. Apakah Anda ingin melanjutkan ke halaman input atau kembali untuk mencoba lagi?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkGreyText
                )
            },
            confirmButton = {
                AppTextButton(
                    onClick = {
                        showDialog = false
                        scannedBillData?.let { data ->
                            if (validateScannedData(data)) {
                                val billDataJson = Gson().toJson(data)
                                val encodedBillDataJson = java.net.URLEncoder.encode(billDataJson, "UTF-8")
                                navController.navigate(
                                    NavRoutes.InputEvent.createRoute(encodedBillDataJson)
                                ) {
                                    popUpTo(NavRoutes.Scan.route) { inclusive = true }
                                }
                            } else {
                                snackbarMessage = "Data tidak valid. Silakan masukkan secara manual."
                            }
                        }
                    },
                    text = "Next",
                    textColor = PinkButtonStroke
                )
            },
            dismissButton = {
                AppTextButton(
                    onClick = { showDialog = false },
                    text = "Back",
                    textColor = MaterialTheme.colorScheme.error
                )
            },
            containerColor = White,
            shape = RoundedCornerShape(16.dp)
        )
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
                selectedScreen = "Scan"
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HomeHeader(
                navController = navController,
                showBackButton = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(60.dp)
                            .padding(16.dp),
                        color = PinkButtonStroke
                    )
                    Text(
                        text = "Image Processing...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkGreyText,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    if (bitmap == null && !showDialog) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .shadow(4.dp, RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Letakkan struk di sini\nPastikan teks item terbaca jelas",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = DarkGreyText.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 24.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        Text(
                            text = "Choose Scan Method",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = DarkGreyText,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AppFilledButton(
                                    onClick = {
                                        cameraButtonScale = 0.95f
                                        if (ContextCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.CAMERA
                                            ) == PackageManager.PERMISSION_GRANTED
                                        ) {
                                            cameraLauncher.launch()
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                        cameraButtonScale = 1f
                                    },
                                    text = "",
                                    textColor = White,
                                    containerColor = PinkButton,
                                    icon = Icons.Default.CameraAlt,
                                    iconTint = White,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .scale(cameraButtonScale)
                                        .animateContentSize(),
                                    height = 80.dp,
                                    cornerRadius = 20.dp,
                                    fontSize = 0
                                )
                                Text(
                                    text = "Scan With Camera",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkGreyText.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .width(100.dp)
                                        .padding(top = 8.dp)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AppFilledButton(
                                    onClick = {
                                        galleryButtonScale = 0.95f
                                        galleryLauncher.launch("image/*")
                                        galleryButtonScale = 1f
                                    },
                                    text = "",
                                    textColor = White,
                                    containerColor = PinkButton,
                                    icon = Icons.Default.PhotoLibrary,
                                    iconTint = White,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .scale(galleryButtonScale)
                                        .animateContentSize(),
                                    height = 80.dp,
                                    cornerRadius = 20.dp,
                                    fontSize = 0
                                )
                                Text(
                                    text = "Import From Gallery",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkGreyText.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .width(100.dp)
                                        .padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun adjustBitmapOrientation(bitmap: Bitmap, uri: Uri?, context: android.content.Context): Bitmap {
    var rotation = 0f
    try {
        if (uri != null) {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                rotation = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
                Log.d("ScanScreen", "Image orientation from Exif: $rotation degrees")
            }
        } else {
            rotation = 90f
            Log.d("ScanScreen", "Camera image, default rotation: 90 degrees")
        }
    } catch (e: IOException) {
        Log.e("ScanScreen", "Failed to read Exif data: ${e.message}")
    }

    return if (rotation != 0f) {
        val matrix = Matrix().apply { postRotate(rotation) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }
}

private fun compressBitmap(bitmap: Bitmap): Bitmap {
    val maxWidth = 1024f
    val maxHeight = 1024f
    val scale = minOf(maxWidth / bitmap.width, maxHeight / bitmap.height)
    if (scale >= 1f) return bitmap

    val matrix = Matrix().apply { postScale(scale, scale) }
    val compressed = Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
    )
    Log.d("ScanScreen", "Compressed bitmap from ${bitmap.width}x${bitmap.height} to ${compressed.width}x${compressed.height}")
    return compressed
}

private fun processImage(
    bitmap: Bitmap,
    onComplete: (ScannedBillData) -> Unit
) {
    val compressedBitmap = compressBitmap(bitmap)
    val image = InputImage.fromBitmap(compressedBitmap, 0)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            Log.d("ScanScreen", "Full Extracted Text: ${visionText.text}")
            val scannedData = parseBillData(visionText)
            Log.d("ScanScreen", "Parsed Data: Items=${scannedData.items.size}, Tax=${scannedData.tax}, ServiceFee=${scannedData.serviceFee}")
            onComplete(scannedData)
        }
        .addOnFailureListener { e ->
            Log.e("ScanScreen", "Text Recognition Failed: ${e.message}")
            onComplete(ScannedBillData(emptyList(), 0L, 0L))
        }
}

private fun validateScannedData(data: ScannedBillData): Boolean {
    val validItems = data.items.all { item ->
        item.name.isNotBlank() &&
                item.quantity > 0 &&
                item.unitPrice >= 0 &&
                item.totalPrice == item.quantity.toLong() * item.unitPrice
    }
    val validTaxAndFee = data.tax >= 0 && data.serviceFee >= 0
    Log.d("ScanScreen", "Validation: ItemsValid=$validItems, TaxAndFeeValid=$validTaxAndFee")
    return validItems && validTaxAndFee
}

private fun parseBillData(visionText: Text): ScannedBillData {
    val items = mutableListOf<Item>()
    var tax = 0L
    var serviceFee = 0L

    val blocks = visionText.textBlocks
    if (blocks.isEmpty()) {
        Log.d("ScanScreen", "No text blocks detected")
        return ScannedBillData(emptyList(), 0L, 0L)
    }

    val itemPattern = Pattern.compile(
        "^(.+?)\\s+(\\d+)\\s*(?:x|\\*|\\s)?\\s*(?:@|Rp\\.?\\s*)?([0-9,.]+)(?:\\s+[0-9,.]+)?$",
        Pattern.CASE_INSENSITIVE
    )

    val taxPattern = Pattern.compile(
        "(?:ppn|tax|pajak)\\s*(?:\\d+%\\s*)?[:=]?\\s*([0-9,.]+)",
        Pattern.CASE_INSENSITIVE
    )

    val serviceFeePattern = Pattern.compile(
        "(?:service|servis|fee|biaya\\s*layanan)\\s*(?:\\d+%\\s*)?[:=]?\\s*([0-9,.]+)",
        Pattern.CASE_INSENSITIVE
    )

    blocks.forEachIndexed { blockIndex, block ->
        Log.d("ScanScreen", "Block $blockIndex: ${block.text}")
        block.lines.forEachIndexed { lineIndex, line ->
            val lineText = line.text.trim()
            Log.d("ScanScreen", "Line $lineIndex: $lineText")

            val itemMatcher = itemPattern.matcher(lineText)
            if (itemMatcher.find()) {
                val name = itemMatcher.group(1)?.trim() ?: ""
                val quantity = itemMatcher.group(2)?.toIntOrNull() ?: 1
                val unitPriceStr = itemMatcher.group(3)?.trim() ?: "0"
                val unitPrice = parsePrice(unitPriceStr)

                if (name.isNotEmpty() && unitPrice > 0 && isLikelyItem(name)) {
                    val totalPrice = unitPrice * quantity.toLong()
                    Log.d("ScanScreen", "Item Detected: Name=$name, Qty=$quantity, UnitPrice=$unitPrice, Total=$totalPrice")
                    items.add(
                        Item(
                            itemId = UUID.randomUUID().toString(),
                            name = name,
                            quantity = quantity,
                            unitPrice = unitPrice,
                            totalPrice = totalPrice
                        )
                    )
                } else {
                    Log.d("ScanScreen", "Item Rejected: Name=$name, UnitPrice=$unitPrice, LikelyItem=${isLikelyItem(name)}")
                }
                return@forEachIndexed
            }

            val taxMatcher = taxPattern.matcher(lineText)
            if (taxMatcher.find()) {
                val taxStr = taxMatcher.group(1)?.trim() ?: "0"
                tax = parsePrice(taxStr)
                Log.d("ScanScreen", "Tax Detected: $tax")
                return@forEachIndexed
            }

            val serviceFeeMatcher = serviceFeePattern.matcher(lineText)
            if (serviceFeeMatcher.find()) {
                val serviceFeeStr = serviceFeeMatcher.group(1)?.trim() ?: "0"
                serviceFee = parsePrice(serviceFeeStr)
                Log.d("ScanScreen", "Service Fee Detected: $serviceFee")
                return@forEachIndexed
            }
        }
    }

    return ScannedBillData(items, tax, serviceFee)
}

private fun parsePrice(priceStr: String): Long {
    try {
        // Hapus spasi dan karakter non-numerik kecuali koma/titik
        var cleanStr = priceStr.replace("[^0-9,.]".toRegex(), "").trim()
        // Normalisasi format: hapus tanda desimal terakhir jika ada (misalnya, "12.500,00" -> "12500")
        val parts = cleanStr.split("[,.]".toRegex())
        if (parts.size > 1) {
            // Asumsikan bagian terakhir adalah desimal (abaikan jika > 2 digit)
            val integerPart = parts.dropLast(1).joinToString("")
            val decimalPart = parts.last()
            if (decimalPart.length <= 2) {
                cleanStr = integerPart + if (decimalPart.isNotEmpty()) decimalPart else ""
            } else {
                cleanStr = integerPart
            }
        }
        // Konversi ke Long
        val number = cleanStr.toLongOrNull() ?: 0L
        Log.d("ScanScreen", "Parsed Price: $priceStr -> $number")
        return number
    } catch (e: Exception) {
        Log.e("ScanScreen", "Failed to parse price: $priceStr, Error: ${e.message}")
        return 0L
    }
}

private fun isLikelyItem(name: String): Boolean {
    val nonItemKeywords = listOf(
        "total", "subtotal", "grand total", "bayar", "kembali", "diskon", "promo",
        "tanggal", "terima kasih", "pembayaran", "cash", "card", "qr"
    )
    val nameLower = name.lowercase()
    val containsNonItem = nonItemKeywords.any { keyword -> nameLower.contains(keyword) }
    Log.d("ScanScreen", "isLikelyItem: Name=$name, ContainsNonItem=$containsNonItem")
    return !containsNonItem
}