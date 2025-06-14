package com.example.billbuddy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = JomhuriaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp, // Sesuai ukuran asli untuk header dan "Active Events"
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp
    ),
    displayLarge = TextStyle(
        fontFamily = JomhuriaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 90.sp, // Sesuai ukuran asli untuk "BillBuddy"
        lineHeight = 96.sp,
        letterSpacing = 0.5.sp
    ),
    displayMedium = TextStyle(
        fontFamily = JomhuriaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp, // Sesuai ukuran asli untuk "IT'S HERE"
        lineHeight = 40.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    )
)