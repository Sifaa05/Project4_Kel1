package com.example.billbuddy.util

import com.example.billbuddy.data.EventData
import com.example.billbuddy.ui.viewModel.SortOption

fun sortEvents(events: List<EventData>, sortOption: SortOption): List<EventData> {
    return when (sortOption) {
        SortOption.NAME_ASC -> events.sortedBy { it.eventName.lowercase() }
        SortOption.NAME_DESC -> events.sortedByDescending { it.eventName.lowercase() }
        SortOption.DATE_ASC -> events.sortedBy { it.timestamp.toDate().time }
        SortOption.DATE_DESC -> events.sortedByDescending { it.timestamp.toDate().time }
    }
}