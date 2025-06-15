package com.example.billbuddy.data

data class User(
    val userId: String = "",
    val email: String = "",
    val name: String = "",
    val username:String? = null,
    val photoUrl: String? = null,
    val eventHistory: List<String> = emptyList()
)