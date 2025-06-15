package com.example.billbuddy.ui.screen.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billbuddy.R
import com.example.billbuddy.ui.components.AppFilledButton
import com.example.billbuddy.ui.theme.JomhuriaFontFamily
import com.example.billbuddy.ui.theme.PinkBackground
import com.example.billbuddy.ui.theme.PinkButtonStroke
import com.example.billbuddy.ui.theme.White
import com.example.billbuddy.ui.theme.WhiteTitle

@Composable
fun AuthenticationScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    // Font kustom
    val jomhuriaFont = JomhuriaFontFamily

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
            Spacer(modifier = Modifier.height(120.dp))

            Text(
                text = "BillBuddy",
                fontFamily = jomhuriaFont,
                fontSize = 100.sp,
                color = WhiteTitle,
                modifier = Modifier.shadow(elevation = 60.dp, shape = RoundedCornerShape(30.dp))
            )

            Spacer(modifier = Modifier.height(150.dp))

            AppFilledButton(
                onClick = onLoginClick,
                text = "Login",
                textColor = White,
                modifier = Modifier.fillMaxWidth(0.65f),
                borderColor = PinkButtonStroke
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppFilledButton(
                onClick = onRegisterClick,
                text = "Register",
                textColor = White,
                modifier = Modifier.fillMaxWidth(0.65f),
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
fun AuthenticationScreenPreview() {
    AuthenticationScreen(
        onLoginClick = {},
        onRegisterClick = {}
    )
}