package com.example.billbuddy.ui.screen.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billbuddy.R
import com.example.billbuddy.ui.components.AppFilledButton
import com.example.billbuddy.ui.theme.BlackText
import com.example.billbuddy.ui.theme.ButtonText
import com.example.billbuddy.ui.theme.KadwaFontFamily
import com.example.billbuddy.ui.theme.KhulaExtrabold
import com.example.billbuddy.ui.theme.KhulaRegular
import com.example.billbuddy.ui.theme.PinkBackground
import com.example.billbuddy.ui.theme.PinkButtonStroke
import com.example.billbuddy.ui.theme.TextFieldBackground

@Composable
fun ForgotPasswordScreen(
    onResetPasswordClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PinkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Forgot Password",
                fontFamily = KhulaExtrabold,
                fontSize = 32.sp,
                color = BlackText.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your email to reset your password",
                fontFamily = KhulaRegular,
                fontSize = 16.sp,
                color = BlackText.copy(alpha = 0.58f)
            )

            Spacer(modifier = Modifier.height(50.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter your Email*", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Mail, contentDescription = "Email Icon") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(60.dp)
                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(40.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = TextFieldBackground,
                    unfocusedContainerColor = TextFieldBackground,
                    focusedIndicatorColor = PinkButtonStroke,
                    unfocusedIndicatorColor = PinkButtonStroke,
                    cursorColor = BlackText,
                    focusedLabelColor = BlackText.copy(alpha = 0.58f),
                    unfocusedLabelColor = BlackText.copy(alpha = 0.58f)
                ),
                shape = RoundedCornerShape(40.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = KadwaFontFamily,
                    color = BlackText.copy(alpha = 0.58f),
                    fontSize = 12.sp
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(80.dp))

            AppFilledButton(
                onClick = { onResetPasswordClick(email) },
                text = "Reset Password",
                textColor = ButtonText.copy(alpha = 0.55f),
                modifier = Modifier.fillMaxWidth(0.6f),
                height = 50.dp,
                fontSize = 18,
                elevation = 4.dp,
                cornerRadius = 25.dp,
                fontWeight = FontWeight.Bold,
                borderColor = PinkButtonStroke
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppFilledButton(
                onClick = onBackClick,
                text = "Back",
                textColor = ButtonText.copy(alpha = 0.55f),
                modifier = Modifier.fillMaxWidth(0.6f),
                height = 50.dp,
                fontSize = 18,
                elevation = 4.dp,
                cornerRadius = 25.dp,
                fontWeight = FontWeight.Bold,
                borderColor = PinkButtonStroke
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.billbuddy_characters),
                contentDescription = "BillBuddy Characters",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(bottom = 5.dp)
            )
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(
        onResetPasswordClick = {},
        onBackClick = {}
    )
}