package com.example.billbuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billbuddy.ui.theme.KadwaFontFamily
import com.example.billbuddy.ui.theme.PinkButton
import com.example.billbuddy.ui.theme.PinkButtonStroke

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
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
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
    fontFamily: FontFamily = KadwaFontFamily,
    fontSize: Int = 25
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(height)
            .shadow(elevation = 15.dp, shape = RoundedCornerShape(60.dp))
            .border(2.dp, PinkButtonStroke, RoundedCornerShape(60.dp)),
        shape = RoundedCornerShape(60.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontFamily = fontFamily,
                fontSize = fontSize.sp,
                color = textColor
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