package com.example.billbuddy.navigation

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home_screen")
    object ListEvent : NavRoutes("list_event_screen")
    object Profile : NavRoutes("profile_screen")
    object Search : NavRoutes("search_screen")
    object InputEvent : NavRoutes("input_event_screen")
    object EventDetail : NavRoutes("event_detail_screen/{eventId}") {
        fun createRoute(eventId: String) = "event_detail_screen/$eventId"
    }
    object AddBuddy : NavRoutes("add_buddy_screen/{eventId}") {
        fun createRoute(eventId: String) = "add_buddy_screen/$eventId"
    }
    object AssignItems : NavRoutes("assign_items_screen/{eventId}/{selectedFriendsParam}") {
        fun createRoute(eventId: String, selectedFriendsParam: String) =
            "assign_items_screen/$eventId/$selectedFriendsParam"
    }
    object Participant : NavRoutes("participant_screen/{eventId}") {
        fun createRoute(eventId: String) = "participant_screen/$eventId"
    }
    object ParticipantBillDetail : NavRoutes("participant_bill_detail_screen/{eventId}/{participantId}") {
        fun createRoute(eventId: String, participantId: String) =
            "participant_bill_detail_screen/$eventId/$participantId"
    }
}