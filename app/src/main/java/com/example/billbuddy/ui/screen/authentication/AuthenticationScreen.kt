package com.example.billbuddy.ui.screen.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billbuddy.R

@Composable
fun AuthenticationScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    // Font kustom
    val jomhuriaFont = FontFamily(Font(R.font.jomhuria_regular))
    val kadwaFont = FontFamily(Font(R.font.kadwa_regular))

    // Warna
    val backgroundColor = Color(0xFFF7ACB8) // Pink background
    val buttonColor = Color(0xFFF4BCC5) // Pink button
    val buttonStrokeColor = Color(0xFFF397AE) // Pink stroke
    val titleColor = Color(0xFFF4F4F4) // White title

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Spacer untuk memberi ruang di atas
            Spacer(modifier = Modifier.height(120.dp))

            // Judul "BillBuddy" dengan efek drop shadow
            Text(
                text = "BillBuddy",
                fontFamily = jomhuriaFont,
                fontSize = 100.sp,
                color = titleColor,
                modifier = Modifier
                    .shadow(elevation =60.dp, shape = RoundedCornerShape(30.dp)),
            )

            // Spacer untuk jarak
            Spacer(modifier = Modifier.height(150.dp))

            // Button "Login" dengan efek drop shadow
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(60.dp)
                    .shadow(elevation = 15.dp, shape = RoundedCornerShape(60.dp))
                    .border(2.dp, buttonStrokeColor, RoundedCornerShape(60.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(60.dp)
            ) {
                Text(
                    text = "Login",
                    fontFamily = kadwaFont,
                    fontSize = 25.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Button "Register" dengan efek drop shadow
            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(60.dp)
                    .shadow(elevation = 15.dp, shape = RoundedCornerShape(60.dp))
                    .border(2.dp, buttonStrokeColor, RoundedCornerShape(60.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(60.dp)
            ) {
                Text(
                    text = "Register",
                    fontFamily = kadwaFont,
                    fontSize = 25.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Gambar di bagian bawah
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