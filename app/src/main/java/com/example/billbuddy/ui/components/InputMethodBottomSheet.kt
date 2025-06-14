package com.example.billbuddy.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billbuddy.navigation.NavRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputMethodBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        val sheetState = androidx.compose.material3.rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Input Method",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                AppTextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                            navController.navigate(NavRoutes.InputEvent.route)
                        }
                    },
                    text = "Manual Input",
                    textColor = MaterialTheme.colorScheme.onBackground,
                    icon = Icons.Default.Create,
                    iconTint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppTextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                            try {
                                navController.navigate(NavRoutes.Scan.route)
                            } catch (e: Exception) {
                                Log.e("InputMethodBottomSheet", "Navigation to ScanScreen failed: ${e.message}", e)
                                // Tampilkan pesan error ke pengguna jika perlu
                            }
                        }
                    },
                    text = "Scan With Camera",
                    textColor = MaterialTheme.colorScheme.onBackground,
                    icon = Icons.Default.CameraAlt,
                    iconTint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                AppTextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    text = "Cancel",
                    textColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}