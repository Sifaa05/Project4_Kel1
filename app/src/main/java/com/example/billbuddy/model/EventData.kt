package com.example.billbuddy.model

data class EventData(
    val eventId: String,
    val creatorId: String,
    val creatorName: String,
    val eventName: String,
    val totalAmount: Long,
    val taxAmount: Long,
    val serviceFee: Long,
    val status: String,
    val timestamp: Long,
    val shareLink: String?,
    val items: List<Item>,
    val participants: List<Participant>
)