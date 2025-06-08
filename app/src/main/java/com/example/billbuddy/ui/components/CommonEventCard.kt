package com.example.billbuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billbuddy.data.EventData
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CommonEventCard(
    event: EventData,
    textColor: Color,
    buttonColor: Color,
    onClick: () -> Unit,
    showDetails: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Gray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👥",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.eventName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                if (showDetails) {
                    Text(
                        text = "Status: ${event.status}",
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor
                    )
                    Text(
                        text = "Tanggal: ${event.timestamp?.toDate()?.let { date ->
                            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
                        } ?: "Unknown"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor
                    )
                    Text(
                        text = "Peserta: ${event.participants.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor
                    )
                }
            }

            AppSmallTextButton(
                onClick = onClick,
                text = "Cek Detail",
                textColor = buttonColor
            )
        }
    }
}