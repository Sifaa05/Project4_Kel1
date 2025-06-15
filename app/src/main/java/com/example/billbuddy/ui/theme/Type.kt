package com.example.billbuddy.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.example.billbuddy.ui.screen.authentication.KhulaFont
//import com.example.billbuddy.ui.screen.authentication.khulaFont
//import com.example.billbuddy.ui.screen.authentication.khulaFontBold

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
        fontSize = 100.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp,
        color = PinkButtonStroke,
        //modifier = Modifier.shadow(elevation = 60.dp, shape = RoundedCornerShape(30.dp))
    ),
    displayLarge = TextStyle(
        fontFamily = JomhuriaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 35.sp,
        lineHeight = 100.sp,
        letterSpacing = 2.sp,
        color = PinkButtonStroke
    ),
    displayMedium = TextStyle(
        fontFamily = KhulaExtrabold,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.5.sp,
        color = PinkButtonStroke
    ),
    labelSmall = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    )
)