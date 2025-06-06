package com.example.billbuddy.data

import com.google.firebase.Timestamp

data class EventData(
    val eventId: String,
    val creatorId: String,
    val creatorName: String,
    val eventName: String,
    val subtotal: Long,
    val totalAmount: Long,
    val taxAmount: Long,
    val serviceFee: Long,
    val status: String,
    val timestamp: Timestamp,
    val shareLink: String?,
    val items: List<Item>,
    val participants: List<Participant>
)