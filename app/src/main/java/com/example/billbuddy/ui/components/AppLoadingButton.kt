package com.example.billbuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppLoadingButton(
    onClick: () -> Unit,
    text: String,
    loadingText: String = "Loading...",
    isLoading: Boolean = false,
    textColor: Color,
    containerColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 25.dp,
    fontSize: Int = 18,
    fontFamily: FontFamily? = null,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
            .shadow(10.dp, RoundedCornerShape(cornerRadius))
            .clickable(enabled = enabled && !isLoading) { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isLoading) loadingText else text,
            color = textColor,
            fontSize = fontSize.sp,
            fontFamily = fontFamily,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}