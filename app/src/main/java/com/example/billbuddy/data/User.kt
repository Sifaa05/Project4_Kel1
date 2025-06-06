package com.example.billbuddy.data

data class User(
    val userId: String,
    val email: String,
    val name: String,
    val photoUrl: String? = null
)