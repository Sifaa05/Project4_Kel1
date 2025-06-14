package com.example.billbuddy.data

data class Participant(
    val id: String,
    val name: String,
    val userId: String?,
    val amount: Long,
    val paid: Boolean,
    val itemsAssigned: List<String>? = null,
    val isCreator: Boolean = false,
    val paymentProofUrl: String? = null
)
