package com.example.billbuddy.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.billbuddy.data.EventData

@Composable
fun LoadingErrorHandler(
    isLoading: Boolean,
    error: String?,
    events: List<EventData>,
    textColor: Color,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    showDetails: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
            error != null -> {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
            else -> {
                EventList(
                    events = events,
                    error = null, // Error sudah ditangani di atas
                    textColor = textColor,
                    buttonColor = MaterialTheme.colorScheme.primary,
                    onEventClick = onEventClick,
                    modifier = Modifier.fillMaxWidth(),
                    showDetails = showDetails
                )
            }
        }
    }
}