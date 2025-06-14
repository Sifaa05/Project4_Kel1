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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.billbuddy.data.Item
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.viewModel.MainViewModel
import com.example.billbuddy.ui.components.AppIconButton
import com.example.billbuddy.ui.components.CommonNavigationBar
import com.google.gson.Gson
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.UUID
import java.util.regex.Pattern

// Data class untuk menyimpan hasil scan
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

    // State untuk animasi tombol
    var cameraButtonScale by remember { mutableStateOf(1f) }
    var galleryButtonScale by remember { mutableStateOf(1f) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { resultBitmap ->
        bitmap = resultBitmap?.let { adjustBitmapOrientation(it) }
        bitmap?.let {
            isProcessing = true
            processImage(it) { result ->
                isProcessing = false
                scannedBillData = result
                if (result.items.isEmpty()) {
                    showDialog = true
                } else {
                    val billDataJson = Gson().toJson(result)
                    val encodedBillDataJson = java.net.URLEncoder.encode(billDataJson, "UTF-8")
                    navController.navigate(
                        NavRoutes.InputEvent.createRoute(encodedBillDataJson)
                    ) {
                        popUpTo(NavRoutes.Scan.route) { inclusive = true }
                    }
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
            bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)?.let { adjustBitmapOrientation(it) }
            bitmap?.let {
                isProcessing = true
                processImage(it) { result ->
                    isProcessing = false
                    scannedBillData = result
                    if (result.items.isEmpty()) {
                        showDialog = true
                    } else {
                        val billDataJson = Gson().toJson(result)
                        val encodedBillDataJson = java.net.URLEncoder.encode(billDataJson, "UTF-8")
                        navController.navigate(
                            NavRoutes.InputEvent.createRoute(encodedBillDataJson)
                        ) {
                            popUpTo(NavRoutes.Scan.route) { inclusive = true }
                        }
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

    // Dialog untuk pemberitahuan jika tidak ada tulisan terdeteksi
    if (showDialog && scannedBillData != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Warning",
                    color = Color(0xFF4A4A4A),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Tidak ada item terdeteksi dari gambar. Apakah Anda ingin melanjutkan ke halaman input atau kembali untuk mencoba lagi?",
                    color = Color(0xFF4A4A4A),
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        scannedBillData?.let { data ->
                            val billDataJson = Gson().toJson(data)
                            val encodedBillDataJson = java.net.URLEncoder.encode(billDataJson, "UTF-8")
                            navController.navigate(
                                NavRoutes.InputEvent.createRoute(encodedBillDataJson)
                            ) {
                                popUpTo(NavRoutes.Scan.route) { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF4CAF50))
                ) {
                    Text("Next", fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFF44336))
                ) {
                    Text("Back", fontSize = 16.sp)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            snackbarMessage = null
        }
    }

    // Palet warna yang lebih harmonis
    val backgroundColor = Color(0xFFFCE4EC) // Soft pink
    val primaryColor = Color(0xFFF06292) // Pink aksen
    val secondaryColor = Color(0xFFEC407A) // Pink lebih tua untuk gradasi
    val textColor = Color(0xFF4A4A4A) // Abu-abu tua untuk teks
    val cardColor = Color.White

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
                .background(backgroundColor)
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconButton(
                    onClick = { navController.popBackStack() },
                    icon = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .shadow(8.dp, RoundedCornerShape(50))
                        .background(primaryColor, shape = RoundedCornerShape(50))
                        .size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Scan Bill",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Area Utama
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(60.dp)
                            .padding(16.dp),
                        color = primaryColor,
                        strokeWidth = 6.dp
                    )
                    Text(
                        text = "Image Processing...",
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    if (bitmap == null && !showDialog) {
                        // Card untuk area panduan
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .shadow(4.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        2.dp,
                                        Brush.linearGradient(
                                            colors = listOf(primaryColor, secondaryColor)
                                        ),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Letakkan struk di sini\nPastikan teks item terbaca jelas",
                                    color = textColor.copy(alpha = 0.7f),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 24.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // Subjudul
                        Text(
                            text = "Chose Scan Methode",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Tombol Pilihan
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Tombol Scan Kamera
                            Card(
                                modifier = Modifier
                                    .size(100.dp)
                                    .shadow(8.dp, RoundedCornerShape(20.dp))
                                    .scale(cameraButtonScale)
                                    .clickable {
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
                                    }
                                    .animateContentSize(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(primaryColor, secondaryColor)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Scan dengan Kamera",
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }

                            // Tombol Impor Galeri
                            Card(
                                modifier = Modifier
                                    .size(100.dp)
                                    .shadow(8.dp, RoundedCornerShape(20.dp))
                                    .scale(galleryButtonScale)
                                    .clickable {
                                        galleryButtonScale = 0.95f
                                        galleryLauncher.launch("image/*")
                                        galleryButtonScale = 1f
                                    }
                                    .animateContentSize(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(primaryColor, secondaryColor)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoLibrary,
                                        contentDescription = "Impor dari Galeri",
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                        }

                        // Label untuk tombol
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Text(
                                text = "Scan With Camera",
                                color = textColor.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = "Import From Galeri",
                                color = textColor.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(100.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun adjustBitmapOrientation(bitmap: Bitmap): Bitmap {
    val matrix = Matrix().apply {
        postRotate(90f)
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private fun processImage(
    bitmap: Bitmap,
    onComplete: (ScannedBillData) -> Unit
) {
    val image = InputImage.fromBitmap(bitmap, 0)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            Log.d("ScanScreen", "Full Extracted Text: ${visionText.text}")
            val scannedData = parseBillData(visionText)
            onComplete(scannedData)
        }
        .addOnFailureListener { e ->
            Log.e("ScanScreen", "Text Recognition Failed: ${e.message}")
            onComplete(ScannedBillData(emptyList(), 0L, 0L))
        }
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
        "^(.+?)\\s+(\\d+)\\s*(?:x|\\*)?\\s*(?:@|Rp)?\\s*([0-9,.]+)(?:\\s+[0-9,.]+)?",
        Pattern.CASE_INSENSITIVE
    )

    val taxPattern = Pattern.compile(
        "(?:ppn|tax)\\s*(?:\\d+%\\s*)?[:=]\\s*([0-9,.]+)",
        Pattern.CASE_INSENSITIVE
    )

    val serviceFeePattern = Pattern.compile(
        "(?:service\\s*(?:fee)?)\\s*(?:\\d+%\\s*)?[:=]\\s*([0-9,.]+)",
        Pattern.CASE_INSENSITIVE
    )

    blocks.forEach { block ->
        Log.d("ScanScreen", "Processing Block: ${block.text}")
        block.lines.forEach { line ->
            val lineText = line.text.trim()
            Log.d("ScanScreen", "Processing Line: $lineText")

            val itemMatcher = itemPattern.matcher(lineText)
            if (itemMatcher.find()) {
                val name = itemMatcher.group(1)?.trim() ?: ""
                val quantity = itemMatcher.group(2)?.toIntOrNull() ?: 1
                val unitPriceStr = itemMatcher.group(3)?.replace("[,.]".toRegex(), "") ?: "0"
                val unitPrice = unitPriceStr.toLongOrNull() ?: 0
                val totalPrice = unitPrice * quantity

                if (name.isNotEmpty() && unitPrice > 0 && isLikelyItem(name)) {
                    Log.d("ScanScreen", "Item Detected: $name, Qty: $quantity, Price: $unitPrice")
                    items.add(
                        Item(
                            itemId = UUID.randomUUID().toString(),
                            name = name,
                            quantity = quantity,
                            unitPrice = unitPrice,
                            totalPrice = totalPrice
                        )
                    )
                }
                return@forEach
            }

            val taxMatcher = taxPattern.matcher(lineText)
            if (taxMatcher.find()) {
                val taxStr = taxMatcher.group(1)?.replace("[,.]".toRegex(), "") ?: "0"
                tax = taxStr.toLongOrNull() ?: 0
                Log.d("ScanScreen", "Tax Detected: $tax")
                return@forEach
            }

            val serviceFeeMatcher = serviceFeePattern.matcher(lineText)
            if (serviceFeeMatcher.find()) {
                val serviceFeeStr = serviceFeeMatcher.group(1)?.replace("[,.]".toRegex(), "") ?: "0"
                serviceFee = serviceFeeStr.toLongOrNull() ?: 0
                Log.d("ScanScreen", "Service Fee Detected: $serviceFee")
                return@forEach
            }
        }
    }

    return ScannedBillData(items, tax, serviceFee)
}

private fun isLikelyItem(name: String): Boolean {
    val nonItemKeywords = listOf(
        "total", "subtotal", "tanggal", "terima", "kasih", "diskon", "pajak", "ppn",
        "service", "fee", "bayar", "kembali", "grand"
    )
    val nameLower = name.lowercase()
    val containsNonItem = nonItemKeywords.any { keyword -> nameLower.contains(keyword) }
    Log.d("ScanScreen", "isLikelyItem: $name -> Contains non-item keyword: $containsNonItem")
    return !containsNonItem
}