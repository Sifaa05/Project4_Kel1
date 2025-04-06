package com.example.billbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.billbuddy.ui.screen.EventDetailScreen
import com.example.billbuddy.ui.theme.BillBuddyTheme
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.example.billbuddy.model.Item
import com.example.billbuddy.model.Participant
import com.example.billbuddy.ui.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = MainViewModel()

        // Buat event baru untuk pengujian
        viewModel.createEvent(
            creatorId = "creator1",
            creatorName = "Alice",
            eventName = "Dinner at Cafe",
            items = listOf(
                Item("item1", "Nasi Goreng", 2, 50000),
                Item("item2", "Es Teh", 3, 15000)
            ),
            participants = listOf(
                Participant("creator1", "Alice", "creator1", 0, false),
                Participant("guest1", "Charlie", null, 0, false)
            ),
            splitType = "even"
        )

        setContent {
            MaterialTheme {
                Surface {
                    EventDetailScreen(eventId = "event_id_1", viewModel = viewModel)
                }
            }
        }
    }
}