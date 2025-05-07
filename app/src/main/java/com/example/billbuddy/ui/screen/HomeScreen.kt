package com.example.billbuddy.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billbuddy.R
import com.example.billbuddy.navigation.NavRoutes
import com.example.billbuddy.ui.MainViewModel
import com.example.billbuddy.ui.components.AppFloatingActionButton
import com.example.billbuddy.ui.components.AppSmallTextButton
import com.example.billbuddy.ui.components.AppTextButton
import com.example.billbuddy.ui.components.CommonNavigationBar
import com.example.billbuddy.ui.components.CommonEventCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    // Warna sesuai desain
    val backgroundColor = Color(0xFFFFDCDC) // Latar pink
    val buttonColor = Color(0xFFFFB6C1) // Warna tombol pink
    val textColor = Color(0xFF4A4A4A) // Warna teks abu-abu tua

    // Definisikan FontFamily untuk font kustom
    val jomhuriaFontFamily = FontFamily(
        Font(R.font.jomhuria_regular)
    )

    // State untuk Bottom Sheet
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val showBottomSheet = remember { mutableStateOf(false) }

    // Ambil daftar event aktif dari ViewModel
    val activeEvents by viewModel.activeEvents.observeAsState(initial = emptyList())
    val error by viewModel.error.observeAsState()

    // Panggil saat layar dimuat
    LaunchedEffect(Unit) {
        viewModel.getActiveEvents()
    }

    // Tampilkan Bottom Sheet jika showBottomSheet bernilai true
    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Judul Bottom Sheet
                Text(
                    text = "Select Input Method",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Opsi 1: Input Manual
                AppTextButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showBottomSheet.value = false
                            navController.navigate(NavRoutes.InputEvent.route)
                        }
                    },
                    text = "Manual Input",
                    textColor = textColor,
                    icon = Icons.Default.Create,
                    iconTint = buttonColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Opsi 2: Scan dengan Kamera
                AppTextButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showBottomSheet.value = false
                            //navController.navigate(NavRoutes.Scan.route)
                        }
                    },
                    text = "Scan With Camera",
                    textColor = textColor,
                    icon = Icons.Default.CameraAlt,
                    iconTint = buttonColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tombol Batal
                AppTextButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showBottomSheet.value = false
                        }
                    },
                    text = "Cancel",
                    textColor = buttonColor
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            AppFloatingActionButton(
                onClick = { showBottomSheet.value = true },
                containerColor = buttonColor,
                contentColor = Color.White
            )
        },
        bottomBar = {
            CommonNavigationBar(
                navController = navController,
                selectedScreen = "Home"
            )
        },
        modifier = Modifier
            .fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hello, Buddy!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { navController.navigate(NavRoutes.Search.route) }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Judul Aplikasi
            Text(
                text = "BillBuddy",
                fontSize = 90.sp,
                fontWeight = FontWeight.Bold,
                color = buttonColor,
                fontFamily = jomhuriaFontFamily
            )
            Text(
                text = "IT'S HERE",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = buttonColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Judul Bagian
            Text(
                text = "Active Events",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Daftar Event Aktif
            if (activeEvents.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(activeEvents) { event ->
                        CommonEventCard(
                            event = event,
                            textColor = textColor,
                            buttonColor = buttonColor,
                            onClick = {
                                if (event.eventId.isNotEmpty()) {
                                    navController.navigate(NavRoutes.EventDetail.createRoute(event.eventId))
                                } else {
                                    Log.e("HomeScreen", "Invalid eventId for event: ${event.eventName}")
                                }
                            },
                            showDetails = false
                        )
                    }
                }
            } else {
                error?.let {
                    Text(
                        text = "Error: $it",
                        color = MaterialTheme.colorScheme.error
                    )
                } ?: Text(
                    text = "Not active events yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
            }
        }
    }
}