package com.example.test3.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test3.R
import com.example.test3.login.fontFamily
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.*
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUp(
    onContinueClicked: (String, String) -> Unit = { _, _ -> },
    onGoogleClicked: () -> Unit = {},
    onLoginClicked: () -> Unit = {},
    onTermsClicked: () -> Unit,
    onPrivacyClicked: () -> Unit

) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(WindowInsets.statusBars.asPaddingValues()) // ✅ Push below status bar
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Logo
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFDFFFD6)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.image2),
                contentDescription = "Logo",
                contentScale = ContentScale.Crop, // ✅ Fill the circle
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Chef Up",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Create an account",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Enter your email to sign up for this app",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("email@domain.com") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Email
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray
            )
        )


        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Create a Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        error?.let {
            Text(text = it, color = Color.Red, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Continue Button
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    error = "Please fill in all fields"
                } else if (password != confirmPassword) {
                    error = "Passwords do not match"
                } else {
                    error = null
                    onContinueClicked(email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Continue", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(color = Color.Gray, modifier = Modifier.weight(1f))
            Text("  or  ", color = Color.Gray)
            Divider(color = Color.Gray, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onGoogleClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F1F1)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continue with Google", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        val annotatedText = buildAnnotatedString {
            append("By clicking continue, you agree to our ")

            pushStringAnnotation(tag = "terms", annotation = "terms")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Terms of Service")
            }
            pop()

            append(" and ")

            pushStringAnnotation(tag = "privacy", annotation = "privacy")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Privacy Policy")
            }
            pop()
        }

        ClickableText(
            text = annotatedText,
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onClick = { offset ->
                annotatedText.getStringAnnotations(tag = "terms", start = offset, end = offset)
                    .firstOrNull()?.let {
                        onTermsClicked() // 👉 Trigger Terms screen
                    }

                annotatedText.getStringAnnotations(tag = "privacy", start = offset, end = offset)
                    .firstOrNull()?.let {
                        onPrivacyClicked() // 👉 Trigger Privacy screen
                    }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onLoginClicked) {
            Text("Click here to Login", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

