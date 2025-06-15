package com.example.billbuddy.ui.screen.authentication

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.billbuddy.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.billbuddy.repository.UserRepository
import com.example.billbuddy.ui.theme.Font
import com.example.billbuddy.ui.theme.FontType
import com.google.firebase.auth.FirebaseAuth


@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onBackClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7ACB8)),
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
                text = "Login",
                fontFamily = Font.getFont(FontType.KHULA_EXTRABOLD),
                fontSize = 32.sp,
                color = Color(0xFF000000).copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hi! Welcome back, you’ve been missed",
                fontFamily = Font.getFont(FontType.KHULA_REGULAR),
                fontSize = 16.sp,
                color = Color(0xFF000000).copy(alpha = 0.58f),
            )

            Spacer(modifier = Modifier.height(50.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {email = it},
                label = { Text("Enter your Email*", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Mail, contentDescription = "Email Icon") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(60.dp)
                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(40.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE2C2C2),
                    unfocusedContainerColor = Color(0xFFE2C2C2),
                    focusedIndicatorColor = Color(0xFFE2C2C2),
                    unfocusedIndicatorColor = Color(0xFFE2C2C2),
                    cursorColor = Color.Black,
                    focusedLabelColor = Color(0xFF000000).copy(alpha = 0.58f),
                    unfocusedLabelColor = Color(0xFF000000).copy(alpha = 0.58f)
                ),
                shape = RoundedCornerShape(40.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = Font.getFont(FontType.KADWA_REGULAR),
                    color = Color(0xFF000000).copy(alpha = 0.58f),
                    fontSize = 12.sp
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                label = { Text("Password*",  fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock Icon") },
                trailingIcon = {
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(
                            imageVector = if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisibility) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(60.dp)
                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(40.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE2C2C2),
                    unfocusedContainerColor = Color(0xFFE2C2C2),
                    focusedIndicatorColor = Color(0xFFE2C2C2),
                    unfocusedIndicatorColor = Color(0xFFE2C2C2),
                    cursorColor = Color.Black,
                    focusedLabelColor = Color(0xFF000000).copy(alpha = 0.58f),
                    unfocusedLabelColor = Color(0xFF000000).copy(alpha = 0.58f)
                ),
                shape = RoundedCornerShape(40.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = Font.getFont(FontType.KADWA_REGULAR),
                    color = Color(0xFF000000).copy(alpha = 0.58f),
                    fontSize = 12.sp
                ),
                singleLine = true
            )

            TextButton(
                onClick = onForgotPasswordClick, // Panggil fungsi navigasi saat diklik
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp, end = 50.dp)
            ) {
                Text(
                    text = "Forgot Password?",
                    fontFamily = Font.getFont(FontType.KADWA_REGULAR),
                    fontSize = 14.sp,
                    color = Color(0xFF000000).copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Login Berhasil", Toast.LENGTH_SHORT).show()
                                    UserRepository().saveUserToFirestore()
                                    onLoginClick(email, password)
                                } else {
                                    Toast.makeText(context, "Login gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4BCC5)),
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(2.dp, Color(0xFFF397AE))
            ) {
                Text(
                    text = "Login",
                    fontFamily = Font.getFont(FontType.KADWA_REGULAR),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF171717).copy(alpha = 0.55f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
                    .shadow(elevation = 4.dp, shape =  RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4BCC5)),
                shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(2.dp, Color(0xFFF397AE))
            ) {
                Text(
                    text = "Back",
                    fontFamily = Font.getFont(FontType.KADWA_REGULAR),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF171717).copy(alpha = 0.55f)
                )
            }

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
fun LoginScreenPreview() {
    LoginScreen(
        onLoginClick = {_, _ ->},
        onBackClick = {},
        onForgotPasswordClick = {}
    )
}