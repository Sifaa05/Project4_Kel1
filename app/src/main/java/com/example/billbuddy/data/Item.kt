package com.example.billbuddy.data

data class Item(
    val itemId: String,
    val name: String,
    val quantity: Int,
    val unitPrice: Long,
    val totalPrice: Long
)