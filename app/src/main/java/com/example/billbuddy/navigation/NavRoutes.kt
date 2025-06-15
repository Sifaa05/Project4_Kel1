package com.example.billbuddy.navigation

sealed class NavRoutes(val route: String) {
    object OnboardingSatu : NavRoutes("onboarding_satu_screen")
    object OnboardingDua : NavRoutes("onboarding_dua_screen") // Tambahkan rute ini
    object OnboardingTiga : NavRoutes("onboarding_tiga_screen")
    object Splash : NavRoutes("splash_screen")
    object Home : NavRoutes("home_screen")
    object ListEvent : NavRoutes("list_event_screen")
    object Profile : NavRoutes("profile_screen")
    object Search : NavRoutes("search_screen")
    object Notification : NavRoutes("notification")
    object InputEvent : NavRoutes("input_event?scannedBillDataJson={scannedBillDataJson}") {
        fun createRoute(scannedBillDataJson: String): String {
            return "input_event?scannedBillDataJson=$scannedBillDataJson"
        }
    }
    object Scan : NavRoutes("scan_screen")
    object EventDetail : NavRoutes("event_detail/{eventId}") {
        fun createRoute(eventId: String): String {
            return "event_detail/$eventId"
        }
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
    object Authentication : NavRoutes("authentication_screen")

    object Login: NavRoutes("login_screen")

    object Register : NavRoutes("register_screen")

    object ForgotPassword : NavRoutes("forgot_password_screen")

    object SharedBill : NavRoutes("sharedBill/{eventId}") {
        fun createRoute(eventId: String) = "sharedBill/$eventId"
    }
}