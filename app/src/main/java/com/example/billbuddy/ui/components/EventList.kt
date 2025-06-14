package com.example.billbuddy.ui.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.billbuddy.data.EventData

@Composable
fun EventList(
    events: List<EventData>,
    error: String?,
    textColor: Color,
    buttonColor: Color,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    showDetails: Boolean = false
) {
    if (events.isNotEmpty()) {
        LazyColumn(
            modifier = modifier.fillMaxWidth()
        ) {
            items(events) { event ->
                CommonEventCard(
                    event = event,
                    textColor = textColor,
                    buttonColor = buttonColor,
                    onClick = {
                        if (event.eventId.isNotEmpty()) {
                            onEventClick(event.eventId)
                        } else {
                            Log.e("EventList", "Invalid eventId for event: ${event.eventName}")
                        }
                    },
                    showDetails = showDetails
                )
            }
        }
    } else {
        error?.let {
            Text(
                text = "Error: $it",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        } ?: Text(
            text = "No active events yet",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}