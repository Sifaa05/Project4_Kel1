package com.example.billbuddy.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billbuddy.ui.theme.PinkButton
import com.example.billbuddy.ui.theme.PinkButtonStroke
import com.example.billbuddy.ui.theme.White
//import com.example.billbuddy.ui.viewModel.SortOption
import com.example.billbuddy.ui.theme.Font
import com.example.billbuddy.ui.theme.FontType

@Composable
fun AppFloatingActionButton(
    onClick: () -> Unit,
    containerColor: Color = PinkButton,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Event"
        )
    }
}

@Composable
fun AppTextButton(
    onClick: () -> Unit,
    text: String,
    textColor: Color,
    icon: ImageVector? = null,
    iconTint: Color? = null,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null && iconTint != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
        }
    }
}

@Composable
fun AppSmallTextButton(
    onClick: () -> Unit,
    text: String,
    textColor: Color,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily = Font.getFont(FontType.KADWA_REGULAR),
    fontSize: Int = 14
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            fontFamily = fontFamily,
            fontSize = fontSize.sp,
            color = textColor
        )
    }
}

@Composable
fun AppIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

@Composable
fun AppFilledButton(
    onClick: () -> Unit,
    text: String,
    containerColor: Color = PinkButton,
    textColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color? = null,
    height: Dp = 60.dp,
    fontFamily: FontFamily = Font.getFont(FontType.KADWA_REGULAR),
    fontSize: Int = 25,
    elevation: Dp = 15.dp,
    cornerRadius: Dp = 60.dp,
    borderWidth: Dp = 2.dp,
    fontWeight: FontWeight = FontWeight.Normal,
    borderColor: Color
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(height)
            .shadow(elevation = elevation, shape = RoundedCornerShape(cornerRadius))
            .border(borderWidth, PinkButtonStroke, RoundedCornerShape(cornerRadius)),
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontFamily = fontFamily,
                fontSize = fontSize.sp,
                color = textColor,
                fontWeight = fontWeight
            )
            if (icon != null && iconTint != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint
                )
            }
        }
    }
}

@Composable
fun AppTextIconButton(
    onClick: () -> Unit,
    text: String,
    textColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(40.dp)
            .background(containerColor, shape = CircleShape)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )
    }
}

//@Composable
//fun SortButton(
//    currentSortOption: SortOption,
//    onSortSelected: (SortOption) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var buttonScale by remember { mutableStateOf(1f) }
//    val scale by animateFloatAsState(targetValue = buttonScale)
//
//    // Tentukan teks dan deskripsi berdasarkan opsi pengurutan saat ini
//    val (sortText, sortDescription) = when (currentSortOption) {
//        SortOption.NAME_ASC -> "A-Z" to "Sort by name A to Z"
//        SortOption.NAME_DESC -> "Z-A" to "Sort by name Z to A"
//        SortOption.DATE_ASC -> "Oldest" to "Sort by date oldest first"
//        SortOption.DATE_DESC -> "Newest" to "Sort by date newest first"
//    }
//
//    // Tentukan opsi pengurutan berikutnya
//    val nextSortOption = when (currentSortOption) {
//        SortOption.DATE_DESC -> SortOption.DATE_ASC
//        SortOption.DATE_ASC -> SortOption.NAME_ASC
//        SortOption.NAME_ASC -> SortOption.NAME_DESC
//        SortOption.NAME_DESC -> SortOption.DATE_DESC
//    }
//
//    AppFilledButton(
//        onClick = {
//            buttonScale = 0.95f
//            onSortSelected(nextSortOption)
//            buttonScale = 1f
//        },
//        text = sortText,
//        containerColor = PinkButton,
//        textColor = White,
//        modifier = modifier
//            .scale(scale)
//            .semantics { contentDescription = sortDescription },
//        icon = Icons.Default.Sort,
//        iconTint = White,
//        height = 48.dp,
//        fontFamily = Font.getFont(FontType.KADWA_REGULAR),
//        fontSize = 14,
//        cornerRadius = 24.dp,
//        borderColor = PinkButtonStroke
//    )
//}