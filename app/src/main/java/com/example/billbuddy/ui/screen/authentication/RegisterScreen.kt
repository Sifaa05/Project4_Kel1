package com.example.billbuddy.ui.screen.authentication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.billbuddy.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.billbuddy.ui.components.AppFilledButton
import com.example.billbuddy.ui.theme.BlackText
import com.example.billbuddy.ui.theme.ButtonText
import com.example.billbuddy.ui.theme.KadwaFontFamily
import com.example.billbuddy.ui.theme.KhulaExtrabold
import com.example.billbuddy.ui.theme.KhulaRegular
import com.example.billbuddy.ui.theme.PinkBackground
import com.example.billbuddy.ui.theme.PinkButtonStroke
import com.example.billbuddy.ui.theme.TextFieldBackground
import com.example.billbuddy.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@Composable
fun RegisterScreen(
    onCreateAccountClick: (String, String) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val userRepository = UserRepository()

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
                text = "Register",
                fontFamily = KhulaExtrabold,
                fontSize = 32.sp,
                color = BlackText.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fill your information below.",
                fontFamily = KhulaRegular,
                fontSize = 16.sp,
                color = BlackText.copy(alpha = 0.58f)
            )

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Enter your Name*", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account Icon") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(60.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(40.dp)),
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

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter your Email*", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Mail, contentDescription = "Email Icon") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(60.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(40.dp)),
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

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password*", fontSize = 12.sp) },
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
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(40.dp)),
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

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password*", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock Icon") },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
                        Icon(
                            imageVector = if (confirmPasswordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisibility) "Hide confirm password" else "Show confirm password"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(60.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(40.dp)),
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

            Spacer(modifier = Modifier.height(30.dp))

            AppFilledButton(
                onClick = {
                    if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                        Toast.makeText(context, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                    } else if (password != confirmPassword) {
                        Toast.makeText(context, "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    // Update displayName in Firebase Authentication
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build()
                                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                                        if (profileTask.isSuccessful) {
                                            user.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                                                if (verificationTask.isSuccessful) {
                                                    // Save user data including name to Firestore
                                                    userRepository.saveUserToFirestore(
                                                        name = name,
                                                        onSuccess = {
                                                            Toast.makeText(context, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                                                            onCreateAccountClick(email, password)
                                                        },
                                                        onFailure = { exception ->
                                                            Toast.makeText(context, "Gagal menyimpan data: ${exception.message}", Toast.LENGTH_SHORT).show()
                                                        }
                                                    )
                                                } else {
                                                    Toast.makeText(context, "Gagal mengirim email verifikasi", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } else {
                                            Toast.makeText(context, "Gagal memperbarui nama: ${profileTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Registrasi gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                },
                text = "Create Account",
                textColor = ButtonText.copy(alpha = 0.55f),
                modifier = Modifier.fillMaxWidth(0.6f),
                height = 50.dp,
                fontSize = 18,
                elevation = 10.dp,
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
                elevation = 10.dp,
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
fun RegisterScreenPreview() {
    RegisterScreen(
        onCreateAccountClick = { _, _ -> },
        onBackClick = {}
    )
}