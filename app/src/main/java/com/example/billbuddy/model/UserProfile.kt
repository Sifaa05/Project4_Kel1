//package com.example.billbuddy.model
//
//data class UserProfile(
//    val name: String,
//    val isPremium: Boolean
//)

package com.example.billbuddy.model

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val isPremium: Boolean
)

// Simulasi user aktif saat ini
object CurrentUser {
    // Dummy user sementara sebelum backend
    val user = UserProfile(
        id = "user_001",
        name = "Application User",
        email = "user@example.com",
        isPremium = true
    )
}
