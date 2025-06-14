package com.example.billbuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.sp
import com.example.billbuddy.ui.theme.JomhuriaFontFamily
import com.example.billbuddy.ui.theme.WhiteTitle
import com.example.billbuddy.ui.theme.PinkBackground
import com.example.billbuddy.ui.theme.WhiteTitle

@Composable
fun AppBranding(
    modifier: Modifier = Modifier,
    isHorizontal: Boolean = false
) {
    val jomhuriaFont = JomhuriaFontFamily
    if (isHorizontal) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            Text(
                text = "BillBuddy",
                fontFamily = jomhuriaFont,
                fontSize = 100.sp,
                color = WhiteTitle,
                modifier = Modifier.shadow(elevation = 60.dp, shape = RoundedCornerShape(30.dp))
            )
//            Text(
//                text = "IT'S HERE",
//                style = MaterialTheme.typography.displayMedium,
//                color = Pink40,
//                textAlign = TextAlign.Start
//            )
        }
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "BillBuddy",
                fontFamily = jomhuriaFont,
                fontSize = 100.sp,
                color = WhiteTitle,
                modifier = Modifier.shadow(elevation = 60.dp, shape = RoundedCornerShape(30.dp))
            )
            Text(
                text = "IT'S HERE",
                style = MaterialTheme.typography.displayMedium,
                color = WhiteTitle,
                textAlign = TextAlign.Center
            )
        }
    }
}