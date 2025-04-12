package com.example.billbuddy.model

data class Item(
    val itemId: String,
    val name: String,
    val quantity: Int,
    val unitPrice: Long,
    val totalPrice: Long
)